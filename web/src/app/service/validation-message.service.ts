import { Injectable } from '@angular/core';

const FIELD_IS_REQUIRED = 'Pole jest <strong>wymagane</strong>';
const UNDEFINED_VALIDATOR = 'Niepoprawny walidator';
const TYPE_CORRECT_EMAIL = 'Wprowadź poprawny email';
const MIN_LENGTH = 'Nie wprowadzono minimalnej ilości znaków: ';

@Injectable({
  providedIn: 'root'
})
export class ValidationMessageService {

  constructor() { }

  messageFor(validator: string, minLength: number = 0) {
    switch(validator) {
      case 'required':
        return FIELD_IS_REQUIRED;
      case 'email':
        return TYPE_CORRECT_EMAIL;
      case 'minlength':
        return MIN_LENGTH + minLength;
      default:
        return UNDEFINED_VALIDATOR;
    }
  }

}
