import { Component } from '@angular/core';

@Component({
  selector: 'app-move-selector',
  imports: [],
  templateUrl: './move-selector.component.html',
  styleUrl: './move-selector.component.css'
})
export class MoveSelectorComponent {
  files: string[] = [
    'arme_anschauen', 'beide_hande_herwinken', 'beruhigen', 'enttauscht', 'fauuste_machen',
    'fauuste_machen_schnell', 'hallo_china', 'hallo_unsicher', 'hallo_weit_links', 'hallo_weit_rechts',
    'hande_wackel_links_rechts', 'hande_wackeln', 'hande_wackeln_beide', 'hande_wackeln_rechts',
    'hande_zeigen_faust', 'herumschauen', 'herwinken', 'joggen', 'koerper_zeigen', 'komm_her_rechter_arm',
    'linke_arm_anschauen', 'linke_hand_anschauen', 'linke_hand_austrecken_faust_bilden', 'linke_hand_herwinken',
    'linke_hand_hoeren', 'linker_arm_an_huefte', 'linker_arm_austrecken_drehen', 'links_schauen', 'links_weit_schauen',
    'muede_links', 'muede_rechts', 'rechte_arm_anschauen', 'rechte_hand_anschauen', 'rechte_hand_ausstrecken_faust_bilden',
    'rechte_hand_herkommen', 'rechte_hand_hoeren', 'rechte_hand_huefte', 'rechter_arm_austrecken_drehen',
    'rechts_schauen', 'rechts_weit_schauen', 'rhytmus', 'tablet_praesentieren', 'tablet_zeigen', 'unten_links_schauen',
    'unten_rechts_schauen', 'unterarme_heben_schnell'
  ];
  selectedFile: string | null = null;

  onFileSelect(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    this.selectedFile = selectElement.value;
  }

  deleteFile(): void {
    this.selectedFile = null;
  }

  // Funktion zum Starten der Datei
  startFile(): void {
    if (this.selectedFile) {
      console.log(`Starte Datei: ${this.selectedFile}`);
      // Hier kannst du die Logik zum Starten der Datei hinzufügen
    }
  }
}
