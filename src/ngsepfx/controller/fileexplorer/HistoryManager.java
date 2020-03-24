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
package ngsepfx.controller.fileexplorer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import ngsep.genome.ReferenceGenome;
import ngsep.genome.ReferenceGenomeFMIndex;

/**
 * @author Juan Camilo Quintero
 * @author Jorge Duitama
 *
 */
public class HistoryManager {
	
	private static final String HISTORY_FILENAME = ".NGSEPFilesHistory.properties";
	
	private static HistoryManager instance = new HistoryManager();
	
	public static final String KEY_GENOME_INDEX_FILE = "FMIndex";
	public static final String KEY_GENOME_FILE = "Genome";
	public static final String KEY_TRANSCRIPTOME_FILE = "Transcriptome";
	public static final String KEY_STRS_FILE = "STRS";
	
	private Map<String, ReferenceGenome> loadedGenomes = new TreeMap<String, ReferenceGenome>();
	
	private Map<String, ReferenceGenomeFMIndex> loadedGenomeIndexes = new TreeMap<String, ReferenceGenomeFMIndex>();
	
	
	
	private String lastGenomeFile;
	
	private String lastGenomeIndexFile;
	
	private String lastSTRsFile;
	
	private String lastTranscriptomeFile;
	
	private HistoryManager () {
		loadHistory();
	}
	public static HistoryManager getInstance () {
		return instance;
	}
	
	public ReferenceGenome getGenome (String filename) throws IOException {
		ReferenceGenome genome = loadedGenomes.get(filename);
		System.out.println("Saved genome: "+genome);
		if(genome == null) {
			//Try to load genome
			genome = new ReferenceGenome(filename);
			loadedGenomes.put(filename, genome);
		}
		lastGenomeFile = filename;
		return genome;
	}
	
	public ReferenceGenomeFMIndex getGenomeIndex (String filename) throws IOException {
		ReferenceGenomeFMIndex index = loadedGenomeIndexes.get(filename);
		System.out.println("Saved index: "+index);
		if(index == null) {
			//Try to load index
			index = ReferenceGenomeFMIndex.loadFromBinaries(filename);
			loadedGenomeIndexes.put(filename, index);
		}
		lastGenomeIndexFile = filename;
		
		return index;
	}
	
	public String getLastGenomeFile() {
		return lastGenomeFile;
	}

	public String getLastGenomeIndexFile() {
		return lastGenomeIndexFile;
	}

	public String getLastSTRsFile() {
		return lastSTRsFile;
	}

	public String getLastTranscriptomeFile() {
		return lastTranscriptomeFile;
	}
	
	public void loadHistory () {
		String homeDirectory = System.getProperty("user.home");
		String historyFile = homeDirectory+ File.separator + HISTORY_FILENAME;
		Properties p = new Properties();
		try (FileReader reader = new FileReader(historyFile)) {
			p.load(reader);
		} catch (IOException e) {
			// History file not found
			return;
		}
		lastGenomeFile = p.getProperty(KEY_GENOME_FILE);
		lastGenomeIndexFile = p.getProperty(KEY_GENOME_INDEX_FILE);
		lastTranscriptomeFile = p.getProperty(KEY_TRANSCRIPTOME_FILE);
		lastSTRsFile = p.getProperty(KEY_STRS_FILE);
	}
	/**
	 * Saves the given path with the given property
	 * @param key to store the given path 
	 * @param path to save
	 */
	public void saveHistory() {
		String homeDirectory = System.getProperty("user.home");
		String historyFile = homeDirectory+ File.separator + HISTORY_FILENAME;
		Properties p = new Properties();
		//Avoid problems due to windows backslashes
		addPathToProperties(p, KEY_GENOME_FILE, lastGenomeFile);
		addPathToProperties(p, KEY_GENOME_INDEX_FILE, lastGenomeIndexFile);
		addPathToProperties(p, KEY_TRANSCRIPTOME_FILE, lastTranscriptomeFile);
		addPathToProperties(p, KEY_STRS_FILE, lastSTRsFile);
		try (PrintStream out=new PrintStream(historyFile)) {
			p.store(out, "Last history files");
			//Set<?> keys = p.keySet();
			//for(Object key2:keys) {
				//out.println(key2.toString()+"="+p.getProperty(key2.toString()));
			//}
		} catch (IOException e) {
			return;
		}
	}
	private void addPathToProperties(Properties p, String key, String path ) {
		String fixedPath = path.replace("\\", "/");
		p.setProperty(key, fixedPath);
		
	}
}
