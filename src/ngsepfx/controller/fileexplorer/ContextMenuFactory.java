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

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import ngsepfx.controller.AnalysisAreaController;
import ngsepfx.controller.MainController;
import ngsepfx.event.NGSEPAnalyzeFileEvent;

/**
 * Factory to create {@link ContextMenu} for the {@link FileExplorerTreeCell}.
 * @author Fernando Reyes
 *
 */
public final class ContextMenuFactory {
	
	/**
	 * Create a {@link ContextMenu} for the given {@link FileExplorerTreeCell}.
	 * @param cell {@link FileExplorerTreeCell}
	 * @return {@link ContextMenu} for the {@link FileExplorerTreeCell}.
	 */
	public static final ContextMenu buildContextMenu(FileExplorerTreeCell cell) {
    	FileTreeItem fileTreeItem = (FileTreeItem) cell.getTreeItem();
		File file = fileTreeItem.getFile();
	    ContextMenu contextMenu = new ContextMenu();
	    String fileInLower = file.getName().toLowerCase();
	    if (file.isDirectory())
	    {
	    	addSimpleMenuItem(contextMenu, cell, "De Novo GBS", "ngsepfx.controller.DeNovoGBSController");
	    	addSimpleMenuItem(contextMenu, cell, "Multiple Reads Aligner", "ngsepfx.controller.ReadsAlignerController");
	    	addSimpleMenuItem(contextMenu, cell, "Multiple Variants Detector", "ngsepfx.controller.VariantsDetectorController");
	    	addSimpleMenuItem(contextMenu, cell, "Variant Files Merge", "ngsepfx.controller.VariantFilesMergeController");
	    	addSimpleMenuItem(contextMenu, cell, "TILLING Individual genotyper", "ngsepfx.controller.TillingPoolsIndividualGenotyperController");
	    	addSimpleMenuItem(contextMenu, cell, "Base Pair Quality Statistics", "ngsepfx.controller.BasePairQualityStatisticsController");
	    	addSimpleMenuItem(contextMenu, cell, "Genomes Aligner", "ngsepfx.controller.GenomesAlignerController");
	    } else if(fileInLower.contains(".vcf")) {
	    	addSimpleMenuItem(contextMenu, cell, "VCF Annotate", "ngsepfx.controller.VCFFunctionalAnnotatorController");
	    	addSimpleMenuItem(contextMenu, cell, "VCF Summary Statistics", "ngsepfx.controller.VCFSummaryStatisticsController");
	    	addSimpleMenuItem(contextMenu, cell, "VCF Filter", "ngsepfx.controller.VCFFilterController");
	    	addSimpleMenuItem(contextMenu, cell, "VCF Converter", "ngsepfx.controller.VCFConverterController");
	    	addSimpleMenuItem(contextMenu, cell, "VCF Diversity Stats", "ngsepfx.controller.VCFDiversityCalculatorController");
	    	addSimpleMenuItem(contextMenu, cell, "VCF Comparator", "ngsepfx.controller.VCFComparatorController");
	    	addSimpleMenuItem(contextMenu, cell, "VCF Genotype Imputation", "ngsepfx.controller.GenotypeImputerController");
	    	addSimpleMenuItem(contextMenu, cell, "VCF Allele Sharing Statistics", "ngsepfx.controller.VCFAlleleSharingStatisticsCalculatorController");
	    	addSimpleMenuItem(contextMenu, cell, "VCF Introgression Analysis", "ngsepfx.controller.VCFWindowIntrogressionAnalysisController");
	    	
	    	addSimpleMenuItem(contextMenu, cell, "Single Individual Haplotyper", "ngsepfx.controller.SingleIndividualHaplotyperController");
	    	addSimpleMenuItem(contextMenu, cell, "VCF Variant Density", "ngsepfx.controller.VCFVariantDensityCalculatorController");
	    	addSimpleMenuItem(contextMenu, cell, "VCF Distance Matrix", "ngsepfx.controller.VCFDistanceMatrixCalculatorController");
	    	addSimpleMenuItem(contextMenu, cell, "VCF Relative Coordinates Translator", "ngsepfx.controller.VCFRelativeCoordinatesTranslatorController");
	    	addSimpleMenuItem(contextMenu, cell, "Mutated Peptides Extractor", "ngsepfx.controller.MutatedPeptidesExtractorController");
	    } else if (fileInLower.endsWith(".txt") || fileInLower.endsWith(".tsv")) {
	    	addSimpleMenuItem(contextMenu, cell, "Reads Demultiplex", "ngsepfx.controller.ReadsDemultiplexController");
	    	addSimpleMenuItem(contextMenu, cell, "TILLING Simulator", "ngsepfx.controller.TillingPopulationSimulatorController");
	    	if(fileInLower.endsWith(".txt")) addSimpleMenuItem(contextMenu, cell, "Hierarchical clustering", "ngsepfx.controller.HierarchicalClusteringController");
	    } else if (fileInLower.contains(".fa") || fileInLower.contains(".fastq") || fileInLower.contains(".fq")) {
	    	addSimpleMenuItem(contextMenu, cell, "Assembler", "ngsepfx.controller.AssemblerController");
	    	addSimpleMenuItem(contextMenu, cell, "Reads Aligner", "ngsepfx.controller.ReadsAlignerController");
	    	addSimpleMenuItem(contextMenu, cell, "K-mers Extractor", "ngsepfx.controller.KmersExtractorController");
	    	//addSimpleMenuItem(contextMenu, cell, "Reads File Errors Corrector", "ngsepfx.controller.ReadsFileErrorsCorrectorController");
	    	if(!fileInLower.contains(".fastq") && !fileInLower.contains(".fq")  ) {
	    		addSimpleMenuItem(contextMenu, cell, "Genome Assembly Sort by Reference", "ngsepfx.controller.AssemblyReferenceSorterController");
	    		addSimpleMenuItem(contextMenu, cell, "Circular Sequences Processor", "ngsepfx.controller.CircularSequencesProcessorController");
	    		addSimpleMenuItem(contextMenu, cell, "Transposable Elements Finder", "ngsepfx.controller.TransposableElementsFinderController");
		    	addSimpleMenuItem(contextMenu, cell, "Genome Assembly Mask", "ngsepfx.controller.GenomeAssemblyMaskController");
		    	addSimpleMenuItem(contextMenu, cell, "Genome Indexer", "ngsepfx.controller.GenomeIndexerController");
		    	addSimpleMenuItem(contextMenu, cell, "Individual Genome Builder", "ngsepfx.controller.VCFIndividualGenomeBuilderController");
	    		addSimpleMenuItem(contextMenu, cell, "Single Reads Simulator", "ngsepfx.controller.SingleReadsSimulatorController");
	    		addSimpleMenuItem(contextMenu, cell, "Single Individual Simulator", "ngsepfx.controller.SingleIndividualSimulatorController");
	    	} else {
	    		addSimpleMenuItem(contextMenu, cell, "FastqFileFilter", "ngsepfx.controller.FastqFileFilterController");
	    	}
	    } else if (fileInLower.endsWith(".bam") || file.getName().endsWith(".sam")|| file.getName().endsWith(".cram")) {
	    	addSimpleMenuItem(contextMenu, cell, "Variants Detector", "ngsepfx.controller.VariantsDetectorController");
	    	addSimpleMenuItem(contextMenu, cell, "Sort Alignments", "ngsepfx.controller.SortAlignmentController");
	    	addSimpleMenuItem(contextMenu, cell, "Coverage Statistics", "ngsepfx.controller.CoverageStatisticsCalculatorController");
	    	addSimpleMenuItem(contextMenu, cell, "Base Pair Quality Statistics", "ngsepfx.controller.BasePairQualityStatisticsController");
	    	addSimpleMenuItem(contextMenu, cell, "Relative Allele Counts Calculator", "ngsepfx.controller.RelativeAlleleCountsCalculatorController");

	    } else if (fileInLower.contains(".gff")) {
	    	addSimpleMenuItem(contextMenu, cell, "Transcriptome Analyzer", "ngsepfx.controller.TranscriptomeAnalyzerController");
	    	addSimpleMenuItem(contextMenu, cell, "Transcriptome Filter", "ngsepfx.controller.TranscriptomeFilterController");
	    	
	    }
	    return contextMenu;
	}

	/**
	 * Add a {@link MenuItem} with the given menuItemLabel and an
	 * {@link ActionEvent} handler that consumes the {@link ActionEvent}
	 * and fires an {@link NGSEPAnalyzeFileEvent} with the 
	 * controllerFullyQualifiedName and the {@link FileTreeItem}'s {@link File}.
	 * @param contextMenu {@link ContextMenu} to be modified.
	 * @param cell {@link FileExplorerTreeCell} to fire 
	 * {@link NGSEPAnalyzeFileEvent} to the {@link MainController}.
	 * @param menuItemLabel text of the {@link MenuItem}.
	 * @param controllerFullyQualifiedName of the {@link AnalysisAreaController}.
	 */
	private static void addSimpleMenuItem(ContextMenu contextMenu, FileExplorerTreeCell cell, String menuItemLabel, String controllerFullyQualifiedName) {
		MenuItem menuItem = new MenuItem(menuItemLabel);
		FileTreeItem fileTreeItem = (FileTreeItem) cell.getTreeItem();
	    menuItem.setOnAction((ActionEvent t) -> {        	
	    	t.consume();
	    	cell.fireEvent(new NGSEPAnalyzeFileEvent(controllerFullyQualifiedName, fileTreeItem.getFile()));
	    });
	    contextMenu.getItems().add(menuItem);
	}
}
