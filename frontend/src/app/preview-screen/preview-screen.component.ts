// ...existing code...
import { Component, inject, signal, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { Tas } from '../models/tas.model';
import { PreviewService } from '../service/preview.service';
import { ActivatedRoute, Params, RouterLink } from '@angular/router';
import { ImageServiceService } from '../service/image-service.service';
import { ImageDto } from '../models/imageDto.model';
import { SlicePipe } from '@angular/common';
import { ImagePreview } from '../models/image-preview.model';
import { ImageResponse } from '../models/image-response.model';

@Component({
  selector: 'app-preview-screen',
  imports: [RouterLink],
  templateUrl: './preview-screen.component.html',
  styleUrl: './preview-screen.component.css',
  standalone: true
})
export class PreviewScreenComponent implements OnInit, OnDestroy {
  @ViewChild('videoPlayer') videoPlayer!: ElementRef<HTMLVideoElement>;
  
  steps = signal<Tas[]>([]);
  previewService = inject(PreviewService);
  activatedRoute = inject(ActivatedRoute);
  actId = 0;

  currentIndex = signal<number>(0);
  isPlaying = signal<boolean>(true);
  progress = signal<number>(0);
  remainingSeconds = signal<number>(0);
  isMuted = signal<boolean>(false); // 🔇 Mute State

  private sceneTimer: any = null;
  private progressTimer: any = null;

  // 🔊 Text-to-Speech
  private speechSynthesis: SpeechSynthesis = window.speechSynthesis;
  private currentUtterance?: SpeechSynthesisUtterance;

  imageService = inject(ImageServiceService);
  image = signal<ImagePreview | null>(null);

  getImage(id: number): void {
    if (!id) return;
    this.imageService.getImageById2(id).subscribe({
      next: (data) => {
        console.log(data.base64Image);
        this.image.set(data);
      },
      error: (error) => {
        console.error('Error fetching image:', error);
      }
    });
  }

  loadCurrentImage(): void {
    const scene = this.currentScene;
    const imgId = this.image()?.id;
    if (imgId) {
      this.getImage(imgId);
    } else {
      this.image.set(null);
    }
  }

  togglePlay(): void {
    this.isPlaying.update(v => !v);
    if (this.isPlaying()) {
      this.startScene();
      this.videoPlayer.nativeElement.play();
      this.speechSynthesis.resume(); // ▶️ Setze Sprachausgabe fort
    } else {
      this.clearTimers();
      this.videoPlayer.nativeElement.pause();
      this.speechSynthesis.pause(); // ⏸️ Pausiere Sprachausgabe
    }
  }

  get currentSceneIndex(): number {
    return this.currentIndex();
  }

  openPreview(imgId: number | undefined): void {
    console.log('open preview for image id', imgId);
  }
  
  ngOnInit(){
    this.activatedRoute.params.subscribe(
      (params: Params) => {
        this.actId = Number(params['id']);
        this.loadSteps(this.actId);
      }
    );
  }

  ngOnDestroy(): void {
    this.clearTimers();
    this.stopSpeech(); // 🔇 Stoppe Sprachausgabe beim Verlassen
  }

  loadSteps(id: number){
    this.previewService.getTagalongStory(id).subscribe({
      next: (data: Tas | Tas[]) => {
        const arr = Array.isArray(data) ? data : [data];
        this.steps.set(arr);
        this.currentIndex.set(0);
        this.ended.set(false);
        this.loadCurrentImage();
        if (this.steps().length > 0 && this.isPlaying()) {
          this.startScene();
        }
      },
      error: (error) => {
        console.error('Error fetching steps:', error);
      }
    });
  }
  ended = signal<boolean>(false);

  startScene(): void {
    if (this.ended()) return;

    this.clearTimers();
    this.progress.set(0);

    const scene = this.currentScene;
    if (!scene || !this.isPlaying()) return;

    if (this.videoPlayer?.nativeElement) {
      this.videoPlayer.nativeElement.load();
      this.videoPlayer.nativeElement.play();
    }

    // 🔊 Text vorlesen
    this.speakText(scene.text || '');

    let durationMs = scene.durationInSeconds ?? 5000;
    if (durationMs < 1000) durationMs = durationMs * 1000;
    const start = performance.now();

    // Set initial remaining seconds
    const totalSeconds = Math.floor(durationMs / 1000);
    this.remainingSeconds.set(totalSeconds);

    this.progressTimer = setInterval(() => {
      const elapsed = performance.now() - start;
      const progress = Math.min(elapsed / durationMs, 1);
      this.progress.set(progress);
      
      // Update remaining seconds
      const remainingSecs = Math.ceil((durationMs - elapsed) / 1000);
      this.remainingSeconds.set(Math.max(0, remainingSecs));
    }, 100);

    this.sceneTimer = setTimeout(() => {
      this.nextScene();
    }, durationMs);
  }

  nextScene(): void {
    this.clearTimers();
    this.stopSpeech(); // 🔇 Stoppe aktuelle Sprachausgabe
    if (this.videoPlayer?.nativeElement) {
      this.videoPlayer.nativeElement.pause();
    }
    const last = this.steps().length - 1;
    if (this.currentIndex() < last) {
      this.currentIndex.update(i => i + 1);
      this.ended.set(false);
      this.loadCurrentImage();
      if (this.isPlaying()) this.startScene();
    } else {
      this.ended.set(true);
      this.isPlaying.set(false);
      this.progress.set(1);
    }
  }

  prevScene(): void {
    this.clearTimers();
    this.stopSpeech(); // 🔇 Stoppe aktuelle Sprachausgabe
    if (this.ended()) this.ended.set(false);
    if (this.currentIndex() > 0) {
      this.currentIndex.update(i => i - 1);
    } else {
      this.currentIndex.set(Math.max(0, this.steps().length - 1));
    }
    this.loadCurrentImage();
    if (this.isPlaying()) this.startScene();
  }

  restartStory(): void {
    this.clearTimers();
    this.stopSpeech(); // 🔇 Stoppe aktuelle Sprachausgabe
    this.ended.set(false);
    this.currentIndex.set(0);
    this.progress.set(0);
    this.isPlaying.set(true);
    this.loadCurrentImage();
    this.startScene();
  }

  private clearTimers(): void {
    if (this.sceneTimer) { clearTimeout(this.sceneTimer); this.sceneTimer = null; }
    if (this.progressTimer) { clearInterval(this.progressTimer); this.progressTimer = null; }
  }

  // 🔊 Text-to-Speech Methoden
  private speakText(text: string): void {
    // Stoppe vorherige Sprachausgabe
    this.stopSpeech();

    // Wenn stumm geschaltet, nicht vorlesen
    if (this.isMuted() || !text || text.trim() === '') {
      return;
    }

    // Erstelle neue Sprachausgabe
    this.currentUtterance = new SpeechSynthesisUtterance(text);
    
    // Konfiguration für deutsche Sprache
    this.currentUtterance.lang = 'de-DE'; // Deutsch
    this.currentUtterance.rate = 0.9; // Sprechgeschwindigkeit (0.1 bis 10)
    this.currentUtterance.pitch = 1; // Tonhöhe (0 bis 2)
    this.currentUtterance.volume = 1; // Lautstärke (0 bis 1)

    // Starte Sprachausgabe
    this.speechSynthesis.speak(this.currentUtterance);
    
    console.log('🔊 Spreche:', text);
  }

  // 🔇 Sprachausgabe stoppen
  private stopSpeech(): void {
    if (this.speechSynthesis.speaking) {
      this.speechSynthesis.cancel();
    }
    this.currentUtterance = undefined;
  }

  // 🔇🔊 Mute/Unmute Toggle
  toggleMute(): void {
    this.isMuted.update(v => !v);
    
    if (this.isMuted()) {
      // Wenn stumm geschaltet, stoppe aktuelle Sprachausgabe
      this.stopSpeech();
      console.log('🔇 Stumm geschaltet');
    } else {
      // Wenn Stummschaltung aufgehoben, spiele aktuellen Text ab (falls gerade eine Szene läuft)
      if (this.isPlaying() && this.currentScene) {
        this.speakText(this.currentScene.text || '');
      }
      console.log('🔊 Laut geschaltet');
    }
  }

  get currentScene(): Tas | null {
    return this.steps()[this.currentIndex()] || null;
  }
}