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
package ngsepfx.event;

import java.io.File;

import javafx.event.EventType;

/**
 * {@link NGSEPEvent} for file analysis.
 * @author Fernando Reyes
 *
 */
@SuppressWarnings("serial")
public class NGSEPAnalyzeFileEvent extends NGSEPEvent {
	
	// Constants.
	
	public static final EventType<NGSEPAnalyzeFileEvent> ANALYZE_FILE = new EventType<NGSEPAnalyzeFileEvent>(NGSEPEvent.ANY,"NGSEP_EVENT_TYPE_ANALYZE_FILE");
	
	// Attributes.
	
	public String controllerFullyQualifiedName;
	
	public File file;
	
	// Constructor.
	
	/**
	 * Instantiate {@link NGSEPAnalyzeFileEvent} used to identify the 
	 * start of an analysis.
	 * @param controllerFullyQualifiedName To be used using reflection.
	 */
	public NGSEPAnalyzeFileEvent(String controllerFullyQualifiedName, File file) {
		super(NGSEPAnalyzeFileEvent.ANALYZE_FILE);
		this.controllerFullyQualifiedName = controllerFullyQualifiedName;
		this.file = file;
	}

}
