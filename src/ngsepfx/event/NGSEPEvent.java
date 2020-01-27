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

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Parent {@link Event} of all {@link NGSEPEvent} events. Containing the
 * parent {@link EventType}.
 * @author Fernando Reyes
 *
 */
@SuppressWarnings("serial")
public class NGSEPEvent extends Event {
	
	// Constants.
	
	public static final EventType<NGSEPEvent> ANY = new EventType<NGSEPEvent>("NGSEP_EVENT_TYPE_ANY");

	// Constructor.
	
	/**
	 * Instantiate an {@link Event} of type {@link NGSEPEvent#ANY}
	 */
	public NGSEPEvent() {
		super(NGSEPEvent.ANY);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Instantiate an {@link Event} of the given {@link EventType}.
	 * @param eventType
	 */
	public NGSEPEvent(EventType<? extends NGSEPEvent> eventType) {
		super(eventType);
	}

}
