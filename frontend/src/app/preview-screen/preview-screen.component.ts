// ...existing code...
import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { Tas } from '../models/tas.model';
import { PreviewService } from '../service/preview.service';
import { ActivatedRoute, Params } from '@angular/router';
import { ImageServiceService } from '../service/image-service.service';
import { ImageDto } from '../models/imageDto.model';
import { SlicePipe } from '@angular/common';
import { ImagePreview } from '../models/image-preview.model';

@Component({
  selector: 'app-preview-screen',
  imports: [],
  templateUrl: './preview-screen.component.html',
  styleUrl: './preview-screen.component.css'
})
export class PreviewScreenComponent implements OnInit, OnDestroy {
  steps = signal<Tas[]>([]);
  previewService = inject(PreviewService);
  activatedRoute = inject(ActivatedRoute);
  actId = 0;

  currentIndex = signal<number>(0);
  isPlaying = signal<boolean>(true);
  progress = signal<number>(0); 

  private sceneTimer: any = null;
  private progressTimer: any = null;

  imageService = inject(ImageServiceService);
  image = signal<ImageDto | null>(null);

  getImage(id: number): void {
    if (!id) return;
    this.imageService.getImageById2(id).subscribe({
      next: (data) => {
        this.image.set(data);
      },
      error: (error) => {
        console.error('Error fetching image:', error);
      }
    });
  }

  loadCurrentImage(): void {
    const scene = this.currentScene;
    const imgId = scene?.image?.id;
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
    } else {
      this.clearTimers();
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
    // don't start if story ended
    if (this.ended()) return;

    this.clearTimers();
    this.progress.set(0);

    const scene = this.currentScene;
    if (!scene || !this.isPlaying()) return;

    let durationMs = scene.durationInSeconds ?? 5000;
    if (durationMs < 1000) durationMs = durationMs * 1000;
    const start = performance.now();

    this.progressTimer = setInterval(() => {
      const elapsed = performance.now() - start;
      this.progress.set(Math.min(elapsed / durationMs, 1));
    }, 100);

    this.sceneTimer = setTimeout(() => {
      this.nextScene();
    }, durationMs);
  }

  nextScene(): void {
    this.clearTimers();
    const last = this.steps().length - 1;
    if (this.currentIndex() < last) {
      this.currentIndex.update(i => i + 1);
      this.ended.set(false);
      this.loadCurrentImage();
      if (this.isPlaying()) this.startScene();
    } else {
      // reached end -> stop playback and mark ended
      this.ended.set(true);
      this.isPlaying.set(false);
      this.progress.set(1);
      // optionally keep currentIndex at last
    }
  }

  prevScene(): void {
    this.clearTimers();
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

  get currentScene(): Tas | null {
    return this.steps()[this.currentIndex()] || null;
  }
}