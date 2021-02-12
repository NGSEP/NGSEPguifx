/*******************************************************************************
 * NGSEP - Next Generation Sequencing Experience Platform
 * Copyright 2016 Jorge Duitama
 *
 * This file is part of NGSEP.
 *
 *     NGSEP is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     NGSEP is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with NGSEP.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ngsepfx.controller.validator;

import java.io.File;

/**
 * @author Fernando Reyes
 *
 */
public class Validator{
	/**
	 * Applies the given validators to the given value
	 * @param validators to apply
	 * @param value to validate
	 * @param label associated to the value to validate
	 * @return ValidationReport with the errors found during the validation
	 */
	public static ValidationReport applyValidations(ValidationEnum[] validators, String value, String label) {
		ValidationReport validationError = new ValidationReport(label);
		if(validators == null) {
			System.err.println("WARN: Field: "+label+" does not have validations");
			return validationError;
		}
		//System.out.println("Value: "+value+" Label: "+label+" validators: "+validators.length);
		if (needsValidation(validators, value)) {
			String error = null;
			for (ValidationEnum validator : validators) {
				error = applyValidation(validator, value);
				//System.out.println("Value: "+value+" Label: "+label+" validator: "+validator.name()+" error: "+error);
				if (error != null) {
					validationError.addError(error);
				}
			}
		}
		return validationError;
	}
	/**
	 * Checks if the given value requires validation according to the given validators
	 * The value should be validated either if it is mandatory or if it is not empty
	 * @param validators to apply to the given value
	 * @param value to validate
	 * @return boolean true if the field should be validated, false otherwise
	 */
	private static boolean needsValidation(ValidationEnum[] validators, String value) {
		boolean isMandatory = false;
		for (ValidationEnum validationEnum : validators) {
			if (validationEnum == ValidationEnum.MANDATORY) {
				isMandatory = true;
			}
		}
		return isMandatory || !value.trim().isEmpty();
	}
	/**
	 * Applies the given validator to the given value
	 * @param validator to apply
	 * @param value to validate
	 * @return String validation error. null if the value passes the validation
	 */
	private static String applyValidation(ValidationEnum validator, String value) {
		boolean empty = (value==null || value.trim().isEmpty());
		if (validator == ValidationEnum.MANDATORY) {
			if (empty) {
				return "Value is mandatory";
			}
			return null;
		}
		if(empty) return null;
		if (validator == ValidationEnum.INPUT_FILE) {
			File file = new File(value);
			if (file.exists()) {	
				if(file.canRead()) {
					return null;
				}
				return "Cannot read file: " + value;
			} 
			return "File does not exist: " + value;
			
		}
		if (validator == ValidationEnum.OUTPUT_FILE) {
			File file = new File(value);
			File dir = file.getParentFile();
			if (dir == null) {					
				return "Directory does not exist";
			} 
			if (!dir.exists()) {					
				return "Directory does not exist: " + dir.getAbsolutePath();
			}
			else if (!dir.canWrite()) {
				return "Missing write permissions for directory: " + dir.getAbsolutePath();
			}
			return null;
		} 
		if (validator == ValidationEnum.INT){
			try {
				Integer.parseInt(value);
				return null;
			} catch (NumberFormatException e) {
				
			}
			return value + " is not an integer.";
		}
		if (validator == ValidationEnum.POSITIVE_NUMBER) {
			try {
				double number = 0;
				number = Double.parseDouble(value);
				if (number >= 0) {
					return null;
				}
			} catch (NumberFormatException e) {
				
			}
			return value + " is not a positive number.";
		}
		if (validator == ValidationEnum.PROPORTION) {
			try {
				double number = 0;
				number = Double.parseDouble(value);
				if (number >= 0 && number <= 1) {
					return null;
				}
			} catch (NumberFormatException e) {
				
			}
			return value + " is not a number in range [0,1]";
		}
		throw new RuntimeException("Unknown validator: " + validator);
	}

}
