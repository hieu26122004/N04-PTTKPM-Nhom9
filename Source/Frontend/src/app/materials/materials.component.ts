import { Component, Input } from '@angular/core';
import { Material } from '../model/class/Material';

@Component({
  selector: 'app-materials',
  standalone: false,
  
  templateUrl: './materials.component.html',
  styleUrl: './materials.component.css'
})
export class MaterialsComponent {
  @Input() materials: Material[] = [];
  @Input() isTeacher: boolean = false;

  constructor() {
  }

  onDelete(materialId:string): void {
  }

  onEdit(materialId:string): void {
  }
}
