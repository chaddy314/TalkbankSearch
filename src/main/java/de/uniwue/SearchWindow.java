package de.uniwue;

import javax.lang.model.element.Element;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class SearchWindow {

    private JTextField searchTextField;
    private JButton searchBtn;
    private JComboBox queryTypeComboBox;
    private JPanel mainPanel;
    private JList corpusList;
    private DefaultListModel listModel;
    private JButton addCorpusBtn;
    private JButton remCorpusBtn;

    private JFileChooser chooser;

    private String[] queryTypes = {"Transcript", "Metadata"};
    private String choosertitle = "select corpus folder..";
    private int listIndex = 0;

    private ArrayList<File> corpusFolders;

    public static void main(String[] args) {


        JFrame frame = new JFrame("Talkbank Search Tool");
        frame.setContentPane(new SearchWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void initialize() {
        for(String type : queryTypes) {
            queryTypeComboBox.addItem(type);
        }
        queryTypeComboBox.setSelectedIndex(0);
        corpusFolders = new ArrayList<File>();
        listModel = new DefaultListModel();
        //corpusList = new JList(listModel);
        corpusList.setModel(listModel);
    }

    public SearchWindow() {
        initialize();
        final UIManager manager = new UIManager();
        try {
            //TODO: maybe do lookandfeel

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"whelp, something went wrong! \n" + e.getLocalizedMessage());
        }

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String msg;
                switch(queryTypeComboBox.getSelectedItem().toString()) {
                    case "Transcript":
                        manager.put("OptionPane.messageForeground", Color.green);
                        msg = "using transcrpt";
                        break;
                    case "Metadata":
                        manager.put("OptionPane.messageForeground", Color.red);
                        msg = "using metadata";
                        break;
                    default:
                        msg = "using nothing";
                        break;
                }

                for(int i = 0; i< listModel.getSize(); i++) {
                    msg += ("\n" + listModel.get(i).toString());
                }

                JOptionPane.showMessageDialog(null,msg);
            }
        });
        addCorpusBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File chosenFile;
                chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle(choosertitle);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                //
                // disable the "All files" option.
                //
                chooser.setAcceptAllFileFilterUsed(false);
                //
                if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
                    System.out.println("getCurrentDirectory(): "
                            +  chooser.getCurrentDirectory());
                    System.out.println("getSelectedFile() : "
                            +  chooser.getSelectedFile());

                    chosenFile = chooser.getSelectedFile();

                    listModel.addElement(chosenFile.toString());
                    //corpusList = new JList(listModel);
                    corpusFolders.add(chosenFile);
                }
                else {
                    System.out.println("No Selection ");
                }
            }


        });
        remCorpusBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int index = corpusList.getSelectedIndex();
                System.out.println(index);
                listModel.remove(index);
                corpusFolders.remove(index);
            }
        });
    }
}
