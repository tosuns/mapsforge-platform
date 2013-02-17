package de.fub.agg2graph.gpseval.output;

import de.fub.agg2graph.gpseval.TestResult;
import de.fub.agg2graph.gpseval.WekaResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class DetailedHTMLOutput extends TestResultsOutput {

    private int mGraphCount = 0;

    @Override
    public void run() {
        String htmlFormat = "<html><head><title>GPSEval Results</title><style>html {font-family: sans-serif;}a {text-decoration: none;color: #145A8F;}section > div {background-color: #B2D1E9;padding: 10px;margin: 10px;-webkit-border-radius: 10px; -moz-border-radius: 10px; -ms-border-radius: 10px; -o-border-radius: 10px; border-radius: 10px;}h1 {font-size: 16px;color: #333;}h2 {font-size: 14px;color: #333;}img {background-color: #fff;padding: 10px;-webkit-border-radius: 10px; -moz-border-radius: 10px; -ms-border-radius: 10px; -o-border-radius: 10px; border-radius: 10px;margin: 0 10px 10px 0;}table {display: inline-block;vertical-align: top;margin: 0 10px 10px 0;border-spacing: 0;}caption {caption-side:top;}th:first-child {-webkit-border-radius: 10px 0 0 0; -moz-border-radius: 10px 0 0 0; -ms-border-radius: 10px 0 0 0; -o-border-radius: 10px 0 0 0; border-radius: 10px 0 0 0;}th:last-child {-webkit-border-radius: 0 10px 0 0; -moz-border-radius: 0 10px 0 0; -ms-border-radius: 0 10px 0 0; -o-border-radius: 0 10px 0 0; border-radius: 0 10px 0 0;}th {background-color: #2558A6;padding: 5px;color: #fff;}td {background-color: #fff;padding: 5px;border-bottom: 1px solid #999;border-right: 1px solid #999;}td:first-child {border-left: 1px solid #2558A6;}td:last-child {border-right: 1px solid #2558A6;}tr:last-child td {border-bottom: 1px solid #2558A6;}tr:last-child td:first-child {-webkit-border-radius: 0 0 0 10px; -moz-border-radius: 0 0 0 10px; -ms-border-radius: 0 0 0 10px; -o-border-radius: 0 0 0 10px; border-radius: 0 0 0 10px;}tr:last-child td:last-child {-webkit-border-radius: 0 0 10px 0; -moz-border-radius: 0 0 10px 0; -ms-border-radius: 0 0 10px 0; -o-border-radius: 0 0 10px 0; border-radius: 0 0 10px 0;}</style></head><body><header><nav><h1>Test cases:</h1><ol>%s</ol></nav></header><section>%s</section></body></html>";
        String navFormat = "<li><a href=\"#results%s\">%s</a></li>";
        String resultFormat = "<div><a name=\"results%s\"></a><h1>%s</h1>%s</div>";
        String graphFormat = "<img src=\"%s.svg\" alt=\"%s\" />";

        StringBuilder nav = new StringBuilder();
        StringBuilder results = new StringBuilder();

        // for each test case result
        for (int i = 0; i < mResults.size(); i++) {
            TestResult result = mResults.get(i);
            StringBuilder resultContent = new StringBuilder();

            // Add navigation link for the current test case
            nav.append(String.format(navFormat, i, result.mCfg.getName()));

            // Generate classifier graphs and tables
            int cvGraphNum = generateClassifierGraph(result.mCrossValidationResults, "Crossvalidation");
            if (cvGraphNum != -1) {
                resultContent.append(String.format(graphFormat, cvGraphNum, "Crossvalidation"));
            }

            int ttGraphNum = generateClassifierGraph(result.mTrainingTestResults, "Training/Test");
            if (ttGraphNum != -1) {
                resultContent.append(String.format(graphFormat, ttGraphNum, "Training/Test"));
            }

            resultContent.append(generateClassifierTable(result.mCrossValidationResults, "Crossvalidation"));
            resultContent.append(generateClassifierTable(result.mTrainingTestResults, "Training/Test"));

            // Generate cross validation graphs/tables for classes
            StringBuilder cvTables = new StringBuilder();
            resultContent.append("<h2>Crossvalidation results</h2>");
            for (WekaResult wResult : result.mCrossValidationResults) {
                int graphNum = generateClassesGraph(wResult);
                if (graphNum != -1) {
                    resultContent.append(String.format(graphFormat, graphNum, "Crossvalidation: " + wResult.mClsName));
                    cvTables.append(generateClassesTable(wResult));
                }
            }
            resultContent.append("<br />");
            resultContent.append(cvTables.toString());

            // Generate test/train graphs/tables for classes
            StringBuilder ttTables = new StringBuilder();
            resultContent.append("<h2>Train/Test results</h2>");
            for (WekaResult wResult : result.mTrainingTestResults) {
                int graphNum = generateClassesGraph(wResult);
                if (graphNum != -1) {
                    resultContent.append(String.format(graphFormat, graphNum, "Train/Test: " + wResult.mClsName));
                    ttTables.append(generateClassesTable(wResult));
                }
            }
            resultContent.append("<br />");
            resultContent.append(ttTables.toString());

            results.append(String.format(resultFormat, i, result.mCfg.getName(), resultContent.toString()));
        }

        // finally put all HTML together and save to file
        String out = String.format(htmlFormat, nav.toString(), results.toString());
        File outFile = mResultsFolder.resolve("index.html").toFile();

        try (FileWriter fw = new FileWriter(outFile); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(out);

        } catch (IOException ex) {
            Logger.getLogger(DetailedHTMLOutput.class.getName()).log(Level.SEVERE, null, ex);
        }
        Logger.getLogger(DetailedHTMLOutput.class.getName()).log(Level.INFO, "Done. Written results to {0}", outFile.getAbsolutePath());
    }

    /**
     * Generate the graph (data-file, gnuPlot-script and svg-image) that shows
     * how many tracks were classified correctly per classifier.
     *
     * @param results
     * @param title
     * @return
     */
    private int generateClassifierGraph(List<WekaResult> results, String title) {
        int graphNum = mGraphCount++;

        // generate Data File
        File dataFile = mResultsFolder.resolve(graphNum + ".graphData").toFile();
        try (FileWriter fw = new FileWriter(dataFile); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Classifier \"correct classified\" \"incorrect classified\"\n");

            for (WekaResult result : results) {
                double correct = result.mEval.correct();
                double incorrect = result.mEval.incorrect();
                double sum = correct + incorrect;

                double correctPerc = sum > 0 ? correct / sum : 0;
                double incorrectPerc = sum > 0 ? incorrect / sum : 0;

                bw.write(String.format(Locale.US, "\"%s\" %f %f\n", result.mClsName.replaceAll("[a-z]", ""), correctPerc, incorrectPerc));
            }

        } catch (IOException ex) {
            Logger.getLogger(DetailedHTMLOutput.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        // genereate GnuPlot script file
        File graphFile = mResultsFolder.resolve(graphNum + ".svg").toFile();
        File scriptFile = mResultsFolder.resolve(graphNum + ".graphScript").toFile();
        try (FileWriter fw = new FileWriter(scriptFile); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("set title \"" + title + "\"\n");
            bw.write("set auto x\n");
            bw.write("set yrange [0:1]\n");
            bw.write("set style data histogram\n");
            bw.write("set style histogram rowstacked\n");
            bw.write("set style fill solid border -1\n");
            bw.write("set boxwidth 0.4\n");
            bw.write("set xtic scale 0\n");
            bw.write("set grid ytics lw 0.5 lc rgb \"#999999\"\n");
            bw.write("set key below\n");
            bw.write("set lmargin 2\n");
            bw.write("set rmargin 0\n");
            bw.write("set tmargin 2\n");
            bw.write("set bmargin 4\n");
            bw.write("set terminal svg size 300,300\n");
            bw.write("set output \"" + graphFile.getAbsolutePath() + "\"\n");
            bw.write("plot \"" + dataFile.getAbsolutePath() + "\" using 2:xtic(1) ti col fc rgb \"#8AC62F\", \"\" u 3 ti col fc rgb \"#BE2F3B\"\n");

        } catch (IOException ex) {
            Logger.getLogger(DetailedHTMLOutput.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }


        // run GnuPlot
        try {
            runGnuPlot(scriptFile.getAbsolutePath());
        } catch (IOException ex) {
            Logger.getLogger(DetailedHTMLOutput.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return graphNum;
    }

    private String generateClassifierTable(List<WekaResult> results, String title) {
        StringBuilder table = new StringBuilder();

        table.append("<table>");
        table.append("<caption>");
        table.append(title);
        table.append("</caption>");
        table.append("<thead>");
        table.append("<tr>");
        table.append("<th>");
        table.append("Classifier");
        table.append("</th>");
        table.append("<th>");
        table.append("Correct cfd.");
        table.append("</th>");
        table.append("<th>");
        table.append("Incorrect cfd.");
        table.append("</th>");
        table.append("</tr>");
        table.append("</thead>");
        table.append("<tbody>");

        for (WekaResult result : results) {
            double correct = result.mEval.correct();
            double incorrect = result.mEval.incorrect();
            double sum = correct + incorrect;

            double correctPerc = sum > 0 ? correct / sum * 100 : 0;
            double incorrectPerc = sum > 0 ? incorrect / sum * 100 : 0;

            table.append("<tr>");
            table.append("<td>");
            table.append(result.mClsName);
            table.append("</td>");
            table.append("<td>");
            table.append(correctPerc).append("%");
            table.append(" (").append((int) correct).append(")");
            table.append("</td>");
            table.append("<td>");
            table.append(incorrectPerc).append("%");
            table.append(" (").append((int) incorrect).append(")");
            table.append("</td>");
            table.append("</tr>");
        }

        table.append("</tbody>");
        table.append("</table>");

        return table.toString();
    }

    /**
     * Generate the graph (data-file, gnuPlot-script and svg-image) that shows
     * the precision/recall-values for each class.
     *
     * @param result
     * @return
     */
    private int generateClassesGraph(WekaResult result) {
        int graphNum = mGraphCount++;

        // generate Data File
        File dataFile = mResultsFolder.resolve(graphNum + ".graphData").toFile();
        try (FileWriter fw = new FileWriter(dataFile); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Class Precision Recall\n");

            int numClasses = result.mClasses.size();
            for (int i = 0; i < numClasses; i++) {
                bw.write(String.format(Locale.US, "\"%s\" %f %f\n", result.mClasses.get(i), result.mEval.precision(i), result.mEval.recall(i)));
            }

        } catch (IOException ex) {
            Logger.getLogger(DetailedHTMLOutput.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        // genereate GnuPlot script file
        File graphFile = mResultsFolder.resolve(graphNum + ".svg").toFile();
        File scriptFile = mResultsFolder.resolve(graphNum + ".graphScript").toFile();
        try (FileWriter fw = new FileWriter(scriptFile); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("set title \"" + result.mClsName + "\"\n");
            bw.write("set auto x\n");
            bw.write("set yrange [0:1]\n");
            bw.write("set style data histogram\n");
            bw.write("set style histogram cluster gap 1\n");
            bw.write("set style fill solid border -1\n");
            bw.write("set boxwidth 0.7\n");
            bw.write("set xtic scale 0\n");
            bw.write("set grid ytics lw 0.5 lc rgb \"#999999\"\n");
            bw.write("set key below\n");
            bw.write("set lmargin 2\n");
            bw.write("set rmargin 0\n");
            bw.write("set tmargin 2\n");
            bw.write("set bmargin 4\n");
            bw.write("set terminal svg size " + (result.mClasses.size() * 175) + ",300\n");
            bw.write("set output \"" + graphFile.getAbsolutePath() + "\"\n");
            bw.write("plot \"" + dataFile.getAbsolutePath() + "\" using 2:xtic(1) ti col fc rgb \"#8AC62F\", \"\" u 3 ti col fc rgb \"#289ECC\"\n");

        } catch (IOException ex) {
            Logger.getLogger(DetailedHTMLOutput.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        // run GnuPlot
        try {
            runGnuPlot(scriptFile.getAbsolutePath());
        } catch (IOException ex) {
            Logger.getLogger(DetailedHTMLOutput.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }

        return graphNum;
    }

    private String generateClassesTable(WekaResult result) {
        StringBuilder table = new StringBuilder();

        table.append("<table>");
        table.append("<caption>");
        table.append(result.mClsName);
        table.append("</caption>");
        table.append("<thead>");
        table.append("<tr>");
        table.append("<th>");
        table.append("Class");
        table.append("</th>");
        table.append("<th>");
        table.append("Precision");
        table.append("</th>");
        table.append("<th>");
        table.append("Recall");
        table.append("</th>");
        table.append("</tr>");
        table.append("</thead>");
        table.append("<tbody>");

        int numClasses = result.mClasses.size();
        for (int i = 0; i < numClasses; i++) {
            table.append("<tr>");
            table.append("<td>");
            table.append(result.mClasses.get(i));
            table.append("</td>");
            table.append("<td>");
            table.append(result.mEval.precision(i));
            table.append("</td>");
            table.append("<td>");
            table.append(result.mEval.recall(i));
            table.append("</td>");
            table.append("</tr>");
        }

        table.append("</tbody>");
        table.append("</table>");

        return table.toString();
    }

    /**
     * Run gnuPlot with the specified script. (currently only Linux is
     * supported)
     *
     * @param script
     * @throws IOException
     */
    private void runGnuPlot(String script) throws IOException {
        if (new File("/usr/bin/gnuplot").exists()) {
            ProcessBuilder pb = new ProcessBuilder("/usr/bin/gnuplot", script);
            pb.directory(mResultsFolder.toFile());
            pb.start();
        }
    }
}
