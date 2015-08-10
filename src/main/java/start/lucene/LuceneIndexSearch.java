/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start.lucene;

import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import crossLinker.GetCrossLinker;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.xmlpull.v1.XmlPullParserException;
import start.GetPTMs;
import theoretical.CPeptides;
import theoretical.Contaminant;
import theoretical.CrossLinkedPeptides;
import theoretical.FragmentationMode;
import theoretical.MonoLinkedPeptides;

/**
 *
 * @author Sule
 */
public class LuceneIndexSearch {

    private static final Logger LOGGER = Logger.getLogger(LuceneIndexSearch.class);
    private File indexFile; // a crosslinked peptide-mass index file
    private int topSearch = 100; // how many topX number of query needs to be called
    private CPeptideSearch cpSearch; // A searching class to run queries
    private PTMFactory ptmFactory;
    private CrossLinker linker;
    private FragmentationMode fragMode;
    private boolean isBranching,
            isContrastLinkedAttachmentOn;

    /**
     *
     * @param indexFile
     * @param ptmFactory
     * @param linker
     * @param fragMode
     * @param isBranching
     * @param isContrastLinkedAttachmentOn
     * @throws IOException
     * @throws Exception
     */
    public LuceneIndexSearch(File indexFile, PTMFactory ptmFactory, CrossLinker linker, FragmentationMode fragMode,
            boolean isBranching, boolean isContrastLinkedAttachmentOn) throws IOException, Exception {
        this.indexFile = indexFile;
        CPeptidesIndex obj = new CPeptidesIndex(indexFile);
        obj.writeIndexFile();
        cpSearch = new CPeptideSearch(indexFile);
        this.ptmFactory = ptmFactory;
        this.linker = linker;
        this.fragMode = fragMode;
        this.isBranching = isBranching;
        this.isContrastLinkedAttachmentOn = isContrastLinkedAttachmentOn;
    }

    public File getIndexFile() {
        return indexFile;
    }

    public int getTopSearch() {
        return topSearch;
    }

    public CPeptideSearch getCpSearch() {
        return cpSearch;
    }

    // range search
    //mod_date:[20020101 TO 20030101] - must be small and bigger
    /**
     * Return selected of CrossLinkedPeptides within a given mass range
     * (inclusive lower and upper mass)
     *
     * @param from smaller value
     * @param to bigger value (must be)
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws XmlPullParserException
     */
    public ArrayList<CrossLinkedPeptides> getQuery(double from, double to) throws IOException, ParseException, XmlPullParserException, IOException {
        ArrayList<CrossLinkedPeptides> selected = new ArrayList<CrossLinkedPeptides>();
        String query = "mass:[" + from + " TO " + to + "]";
        TopDocs topDocs = cpSearch.performSearch(query, topSearch);
        ScoreDoc[] res = topDocs.scoreDocs;
        while (res.length == topSearch) {
            topSearch += 100;
            LOGGER.debug("indeed full.." + topSearch);
            topDocs = cpSearch.performSearch(query, topSearch);
            res = topDocs.scoreDocs;
        }
        LOGGER.debug("total result=" + res.length);
        for (ScoreDoc re : res) {
            Document doc = cpSearch.getDocument(re.doc);
            CrossLinkedPeptides cp = getCPeptides(doc);
            selected.add(cp);
        }
        return selected;
    }

    /**
     * This method generates a CPeptides object after reading a file
     *
     * @param line
     * @param ptmFactory
     * @param linker
     * @param fragMode
     * @param isBranching
     * @param isContrastLinkedAttachmentOn
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private CrossLinkedPeptides getCPeptides(Document doc) throws XmlPullParserException, IOException {
        CrossLinkedPeptides selected = null;
        String proteinA = doc.get("proteinA"),
                proteinB = doc.get("proteinB"), // proteinB name
                peptideAseq = doc.get("peptideAseq"),
                peptideBseq = doc.get("peptideBseq"),
                linkA = doc.get("linkA"),
                linkB = doc.get("linkB"),
                fixedModA = doc.get("fixModA"),
                fixedModB = doc.get("fixModB"),
                variableModA = doc.get("varModA"),
                variableModB = doc.get("varModB"),
                mass = doc.get("mass"),
                label = "";
        if (!proteinA.startsWith("contaminant")) {
            label = doc.get("label");
        }
        // linker positions...
        // This means a cross linked peptide is here...
        if (!proteinB.equals("-")) {
            linker.setIsLabeled(Boolean.parseBoolean(label));
            Integer linkerPosPeptideA = Integer.parseInt(linkA),
                    linkerPosPeptideB = Integer.parseInt(linkB);
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false),
                    fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModB, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA),
                    ptms_peptideB = new ArrayList<ModificationMatch>(fixedPTM_peptideB);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true),
                    variablePTM_peptideB = GetPTMs.getPTM(ptmFactory, variableModB, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            ptms_peptideB.addAll(variablePTM_peptideB);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseq, ptms_peptideA),
                    peptideB = new Peptide(peptideBseq, ptms_peptideB);
            if (peptideA.getSequence().length() > peptideB.getSequence().length()) {
                // now generate peptide...
                CPeptides tmpCpeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linkerPosPeptideA, linkerPosPeptideB, fragMode, isBranching, isContrastLinkedAttachmentOn);
                selected = tmpCpeptide;
            } else {
                CPeptides tmpCpeptide = new CPeptides(proteinB, proteinA, peptideB, peptideA, linker, linkerPosPeptideB, linkerPosPeptideA, fragMode, isBranching, isContrastLinkedAttachmentOn);
                selected = tmpCpeptide;
            }
        } // This means only monolinked peptide...    
        else if (!proteinA.startsWith("contaminant")) {
            Integer linkerPosPeptideA = Integer.parseInt(linkA);
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseq, ptms_peptideA);
            MonoLinkedPeptides mP = new MonoLinkedPeptides(peptideA, proteinA, linkerPosPeptideA, linker, fragMode, isBranching);
            selected = mP;
        } else if (proteinA.startsWith("contaminant")) {
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseq, ptms_peptideA);
            Contaminant mP = new Contaminant(peptideA, proteinA, fragMode, isBranching);
            selected = mP;
        }
        return selected;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        File indexFile = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\databases\\test\\lucene/target_Rdecoy_cam_plectin_cxm_both_index.txt"),
                modsFile = new File("C:/Users/Sule/Documents/NetBeansProjects/CrossLinkedPeptides/src/resources/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);
        CrossLinker linker = GetCrossLinker.getCrossLinker("DSS", true);
        FragmentationMode fragMode = FragmentationMode.HCD;
        boolean isBranching = false,
                isContrastLinkedAttachmentOn = false;

        LuceneIndexSearch o = new LuceneIndexSearch(indexFile, ptmFactory, linker, fragMode, isBranching, isContrastLinkedAttachmentOn);
        ArrayList<CrossLinkedPeptides> query = o.getQuery(1500, 1700);
        for (CrossLinkedPeptides q : query) {
            System.out.println(q.toPrint());
        }

    }
}