import { Component, ElementRef, inject, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RouterOutlet, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { STORY_URL } from '../app.config';
import { IGameType, IStep, ITagalongStory } from '../../models/tagalongstories.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { map, Observable } from 'rxjs';

@Component({
  selector: 'app-createstory',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './createstory.component.html',
  styleUrl: './createstory.component.css',
})
export class CreatestoryComponent {
  private baseUrl = inject(STORY_URL);
  private http = inject(HttpClient);
  public duration = [5, 10, 15];
  public moves = [
    'emote_hurra',
    'essen',
    'gehen',
    'hand_heben',
    'highfive_links',
    'highfive_rechts',
    'klatschen',
    'strecken',
    'umher_sehen',
    'winken',
  ];
  public moveNames = [
    'Hurra',
    'Essen',
    'Gehen',
    'Hand heben',
    'Highfive links',
    'Highfive rechts',
    'Klatschen',
    'Strecken',
    'Umher sehen',
    'Winken',
  ];

  @ViewChild('tbody') tbody!: ElementRef;

  private defaultGameType : IGameType = {
    id: "TAG_ALONG_STORY",
    name: "Mitmachgeschichten"
  }

  public tagalongstory: ITagalongStory = {
    id: 0,
    name: 'string',
    icon: 'string',
    gameType: this.defaultGameType,
    enabled: true,
  };


  public steps: IStep[] = [];
  public uploadedImageUrl: string =
    'https://fakeimg.pl/600x400?text=Bild+Hochladen';

  public uploadedStoryImageUrl: string =
    'https://fakeimg.pl/600x400?text=Bild+Hochladen';

  private id: number = 0;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {


    this.route.params.subscribe((params) => {
      this.id = params['id'];
    });

    if (this.id) {
      this.http
        .get<ITagalongStory>(this.baseUrl + '/' + this.id)
        .subscribe((story) => {
          this.tagalongstory = story;
          this.uploadedImageUrl = 'data:image/png;base64,' + story.icon;
          
        });

      this.http
        .get<IStep[]>(this.baseUrl + '/' + this.id + '/steps')
        .subscribe((s) => {
          this.steps = s.sort((a, b) => a.index - b.index);
        });
    }
    
  }

  public addStep() {
    this.getLatestTagId().subscribe((latestId: number) => {
      if (!this.id) {
        this.id = latestId;
      }
      
      this.steps.push({
        duration: 0,
        moveNameAndDuration: '',
        text: '',
        image: 'https://fakeimg.pl/600x400?text=Bild+Hochladen',
        index: this.steps.length + 1,
        tagAlongStoryId: this.id,
      });
  
      console.log('Step added:', this.steps[this.steps.length - 1]);
      console.log('All steps:', this.steps);
    });
  }
  

  private getLatestTagId(): Observable<number> {
    return this.http.get<any[]>(this.baseUrl).pipe(
      map((data) => {
        let dataLen = data.length;
        return data[dataLen - 1].id + 1;
      })
    );
  }

  public uploadStoryIcon() {
    const fileInput = document.getElementById(
      'storyIconInput'
    ) as HTMLInputElement;
    fileInput.click();
  }

  public handleStoryIcon(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.uploadedImageUrl = e.target.result; // Set the uploaded image as the src
      };
      reader.readAsDataURL(file);
    }
  }

  public uploadStepIcon(index: number) {
    console.log("WAS GEHT AB");
    
    const fileInput = document.getElementById(
      'stepIconInput-' + index
    ) as HTMLInputElement;
    fileInput.click();
  }

  public handleStepIcon(event: Event, index: number) {
    
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.steps[index].image = e.target.result;
        console.log(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  }

  public uploadTagalongStory(name: string) {
    this.getLatestTagId().subscribe((latestId: number) => {
      // Set the ID only after getting the latest ID
      if (!this.id) {
        this.id = latestId;
      }
  
      // Now that we have the ID, set the other properties and upload
      this.tagalongstory.id = this.id;
      this.tagalongstory.enabled = true;
      this.tagalongstory.icon = this.uploadedImageUrl.replace(/^data:image\/[a-zA-Z]+;base64,/, '');
      this.tagalongstory.name = name;

      // Send POST request to the backend
      this.http.post('http://localhost:8080/api/tagalongstories', this.tagalongstory).subscribe(
        (response) => {
          console.log('TagalongStory uploaded successfully:', response);
        },
        (error) => {
          console.error('Error uploading TagalongStory:', error);
        }
      );
    });
  }
  
  public getImageSource(image: string): string {
    if (image.startsWith('data:image/jpeg;base64,')) {
      return image; // Image already has the prefix
    }
    return 'data:image/jpeg;base64,' + image; // Add the prefix if missing
  }
  

}