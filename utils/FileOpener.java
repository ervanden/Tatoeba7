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

    String outputPathName;
    BufferedWriter outputStream;
    BufferedReader inputStream;
    int outputFileGeneration;
    int outputLinesWritten;
    Integer maxOutputLines;

    public boolean openOutputFile(Integer maxOutputLines) {
        this.maxOutputLines = maxOutputLines;
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
            fileChooser.setDialogTitle("Chose output folder");
            int retval = fileChooser.showOpenDialog(null);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                dirName = f.getAbsolutePath();
                if ((dirName == null) || (dirName.length() == 0)) {
                    return false;
                }
            }

            fileName = (String) JOptionPane.showInputDialog(
                    null,
                    "Output file name",
                    "Enter the name of the output file",
                    JOptionPane.PLAIN_MESSAGE
            );

            if ((fileName == null) || (fileName.length() == 0)) {
                return false;
            }
            outputPathName = dirName + "\\" + fileName;
            outputPathName = outputPathName.replaceAll("[.]...$", "");   //remove extension
            outputFileGeneration = 1;
        }

        if (action.equals("next")) {
            outputFileGeneration++;
        }

        if (maxOutputLines != null) {
            fileName = outputPathName + "-" + outputFileGeneration + ".txt";
        } else {
            fileName = outputPathName + ".txt";
        }

        File f = new File(fileName);
        if (f.exists()) {
            int reply = JOptionPane.showConfirmDialog(null, "File exists. Overwrite?", f.getPath(), JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.NO_OPTION) {
                return false;
            }
        }

        try {
            MsgTextPane.write("Opening " + fileName);
            OutputStream is = new FileOutputStream(f);
            OutputStreamWriter isr = new OutputStreamWriter(is, "UTF-8");
            outputStream = new BufferedWriter(isr);
            outputLinesWritten = 0;
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
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ioe) {
            MsgTextPane.write("io exception closing in fileClose()");
        }
    }



    public void openInputFile() {
        String fileName = "";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select a file");
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
    
        public void closeInputFile() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ioe) {
            MsgTextPane.write("io exception closing in fileClose()");
        }
    }
}
