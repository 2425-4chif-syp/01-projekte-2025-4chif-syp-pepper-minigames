import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatIconModule } from '@angular/material/icon';
import {MatRadioModule} from '@angular/material/radio';
import {MatDividerModule} from '@angular/material/divider';
import { NgxDropzoneModule } from 'ngx-dropzone';
import { Step, Storys, StorysService } from '../storys.service';
import {CdkDragDrop, CdkDropList, CdkDrag, moveItemInArray} from '@angular/cdk/drag-drop';


@Component({
  selector: 'app-new-story',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MatSlideToggleModule, MatIconModule, MatRadioModule, MatDividerModule, NgxDropzoneModule],
  templateUrl: './new-story.component.html',
  styleUrls: ['./new-story.component.scss']
})
export class NewStoryComponent implements OnInit {

  public duration = [5, 10, 15]
  public moves = ["emote_hurra", "essen", "gehen", "hand_heben", "highfive_links", "highfive_rechts", "klatschen", "strecken", "umher_sehen", "winken"]
  public moveNames = ["Hurra", "Essen", "Gehen", "Hand heben", "Highfive links", "Highfive rechts", "Klatschen", "Strecken", "Umher sehen", "Winken"]

  files: string[] = [];
  rowIndex = 0;
  isNew = true;
  saveEnabled = false;
  addNewRowEnabled = false;

  constructor(private cd: ChangeDetectorRef,
    private fb: FormBuilder,
    private stories: StorysService,
    private router: Router,
    private activatedRoute: ActivatedRoute) {
  }

  drop(event: CdkDragDrop<string[]>) {
    // moveItemInArray(this.rows., event.previousIndex, event.currentIndex);
  }

  up(index: number) {
    if (index > 0) {
      const tmp = this.rows.at(index - 1);
      this.rows.setControl(index - 1, this.rows.at(index));
      this.rows.setControl(index, tmp);
    }
  }

  down(index: number) {
    if (index < this.rows.length - 1) {
      const tmp = this.rows.at(index + 1);
      this.rows.setControl(index + 1, this.rows.at(index));
      this.rows.setControl(index, tmp);
    }
  }

  form = this.fb.group({
    id: [0],
    rows: this.fb.array([]),
    name: [''],
    image: ['']
  });

  get rows() {
    return this.form.controls["rows"] as FormArray;
  }

	onSelect(event: any) {
		console.log(event);
		this.files.push(...event.addedFiles);
	}

	onRemove(event: any) {
		console.log(event);
		this.files.splice(this.files.indexOf(event), 1);
	}

  splitMoveNameAndDuration(moveNameAndDuration: string): string[] {
    const arr = moveNameAndDuration.split("_");
    const duration = arr[arr.length - 1];
    let moveName = "";
    for (let index = 0; index < arr.length - 1; index++) {
      const element = arr[index];
      moveName += element + "_";
    }
    moveName = moveName.substring(0, moveName.length - 1);
    return [duration, moveName];
  }


  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const id = params['id'];
      if (id != undefined){
        this.isNew = false;
        // editiermodus
        this.stories.getTagalongstory(id).subscribe(data => {
          var image = data.storyIcon;
          console.log("Loaded Raw MG-Image: " + image);
          // if(!image.includes("data:image/png;base64,")){
          //   image ="data:image/png;base64,"+image;
          // }
          // console.log("Loaded MG-Image: " + image);
          this.form.patchValue({
            id: data.id,
            name: data.name,
            image: image
          });
          for (let index = 0; index < data.steps.length; index++) {
            const element = data.steps[index];
            const split = this.splitMoveNameAndDuration(element.moveNameAndDuration);
            var image = element.image;
            if(!image.includes("data")){
              image = "data:image/png;base64,"+image;
            }
            this.rows.push(this.fb.group({
              id: [element.id],
              duration: [element.duration],
              text: [element.text],
              move: split[1],
              image: image
            }));
          }
        });
      }else{
        this.addRow();
      }
    });

    this.form.valueChanges.subscribe(data => {
      this.saveEnabled = this.form.get('name')?.value != "" && this.form.get('image')?.value != "";
    });
    this.rows.valueChanges.subscribe(data => {
      let en = true;
      for (let index = 0; index < this.rows.length; index++) {
        const element = this.rows.at(index);
        const text = element.get('text')?.value as string;
        const duration = element.get('duration')?.value as number;
        const move = element.get('move')?.value as string;
        const id = element.get('id')?.value as number;
        const image = element.get('image')?.value as string;
        if (text == "" || duration == 0 || move == "" || image == ""){
          en = false;
        }
      }
      this.addNewRowEnabled = en;
    });
  }

  // Methode zum Hinzufügen einer Zeile
  addRow() {
    this.rows.push(this.fb.group({
      id: [this.rowIndex],
      duration: [0],
      text: [''],
      move: [''],
      image: ['']
    }));
    this.rowIndex++;
  }


  removeRow(index: number) {
    this.rows.removeAt(index);

  }

  save(){
    const stepArray : Step[] = [];
    for (let index = 0; index < this.rows.length; index++) {
      const element = this.rows.at(index);

      const text = element.get('text')?.value as string;
      const duration = element.get('duration')?.value as number;
      const move = element.get('move')?.value as string;
      const id = element.get('id')?.value as number;
      var image =element.get('image')?.value as string;
      if(!image.includes("data")){
        image = "data:image/png;base64,"+image;
      }
      stepArray.push({
        id: id,
        text: text,
        duration: duration,
        //moveNameAndDuration: move + "_" + duration,
        moveNameAndDuration: move,
        image: image
      });
    }

    const id = this.form.get('id')?.value as number;
    var image = this.form.get('image')?.value as string;
    if(!image.includes("data")){
      image = "data:image/png;base64,"+image;
    }
    const model: Storys = {
      name: this.form.get('name')?.value as string,
      steps: stepArray,
      id: id,
      isEnabled: true,
      storyIcon: image
    };

    console.log("MODEL: " + model);
    console.log()

    if(this.isNew){
      this.stories.postgetTagalongstories(model).subscribe(_ => {
        this.router.navigate(['/story-selection']);
      });
    }else{
      this.stories.putTagalongstory(model).subscribe(_ => {
        this.router.navigate(['/story-selection']);
      });
    }
  }

  onFileSelected(event: any) {
    let file: File = event.target.files[0];
    let reader = new FileReader();
    reader.onload = () => {
      const base64 = (reader.result as string);
      this.form.patchValue({
        image: base64
      });
    }
    reader.readAsDataURL(file);
  }

  onRowFileSelected(event: any, index: number) {
    const file: File = event.target.files[0];
    const reader = new FileReader();

    reader.onload = () => {
      const base64 = reader.result as string;
      this.rows.at(index).patchValue({
        image: base64
      });
    }

    reader.readAsDataURL(file);
  }



  deleteImage() {
    // Setze den Wert des Bildformulars auf einen leeren String oder null, je nachdem, wie dein Formular strukturiert ist
    this.form.get('image')?.setValue('');
  }
}
