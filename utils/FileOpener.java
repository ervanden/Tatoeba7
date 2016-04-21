package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileOpener {

    String outputFileName;
    BufferedWriter outputStream;
    BufferedReader inputStream;
    int outputFileGeneration;
    int outputLinesWritten;

    public boolean openOutputFile() {
        return openOutputFileAction("new");
    }

    public boolean openOutputFileAction(String action) {

        // when the output file gets too large, it is closed and a new one is openened
        // action == "new"  : user selects an output file name
        // action == "next" : switch to a new file. outputFileGeneration is appended to the name
        String fileName = "";
        if (action.equals("new")) {

            String dirName = "";
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setApproveButtonText("Select");
            fileChooser.setDialogTitle("Output folder for collected words");
            int retval = fileChooser.showOpenDialog(null);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                dirName = f.getAbsolutePath();
                fileName = dirName + "\\collectedWords";
            }

            fileName = (String) JOptionPane.showInputDialog(
                    null,
                    "Output file name",
                    "Confirm or change file name",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    fileName);

            if ((fileName == null) || (fileName.length() == 0)) {
                return false;
            }
            outputFileName = fileName;
            outputFileGeneration = 1;
        }

        if (action.equals("next")) {
            outputFileGeneration++;
        }

        fileName = outputFileName + "-" + outputFileGeneration + ".txt";

        try {
            OutputStream is = new FileOutputStream(new File(fileName));
            OutputStreamWriter isr = new OutputStreamWriter(is, "UTF-8");
            outputStream = new BufferedWriter(isr);
            outputLinesWritten = 0;
            MsgTextPane.write("Start writing to " + fileName);
            return true;
        } catch (IOException fnf) {
            MsgTextPane.write("exception in FileOpenerOut");
            return false;
        }

    }

    public void writeln(String s) {
        try {
            outputStream.write(s);
            outputStream.newLine();
            outputLinesWritten++;
        } catch (IOException ioe) {
            MsgTextPane.write("io exception in fileWriter()");
        }

        if (outputLinesWritten >= 100000) {
            closeOutputFile();
            openOutputFileAction("next");
        }
    }

    public void closeOutputFile() {
        try {
            if (outputStream!=null){
            outputStream.close();
            }
        } catch (IOException ioe) {
            MsgTextPane.write("io exception closing in fileClose()");
        }
    }

    public void closeInputFile() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ioe) {
            MsgTextPane.write("io exception closing in fileClose()");
        }
    }

    public void openInputFile() {
        String fileName = "";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select the file with URLs");
        int retval = fileChooser.showOpenDialog(null);
        if (retval == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            fileName = f.getAbsolutePath();
        }

        if ((fileName != null) && (fileName.length() > 0)) {
            File initialFile = new File(fileName);
            try {
                InputStream is = new FileInputStream(initialFile);
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                inputStream = new BufferedReader(isr);
            } catch (IOException fnf) {
                MsgTextPane.write("exception in FileOpenerIn");
            }

        }

    }

    public String readLine() {
        try {
            return inputStream.readLine();
        } catch (IOException io) {
            MsgTextPane.write("IO exception when reading file");
            return null;
        }
    }
}
