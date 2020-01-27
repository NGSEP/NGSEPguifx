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

import javafx.event.EventType;

/**
 * @author Fernando Reyes
 *
 */
@SuppressWarnings("serial")
public class NGSEPRefreshFileExplorerEvent extends NGSEPEvent {
	
	public static final EventType<NGSEPRefreshFileExplorerEvent> REFRESH = new EventType<NGSEPRefreshFileExplorerEvent>(NGSEPEvent.ANY,"NGSEP_EVENT_TYPE_REFRESH");
	
	public NGSEPRefreshFileExplorerEvent() {
		super(NGSEPRefreshFileExplorerEvent.REFRESH);
	}
}
