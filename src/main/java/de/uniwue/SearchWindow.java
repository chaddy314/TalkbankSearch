package de.uniwue;

import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

public class SearchWindow {
    private JFrame frame;

    private JTextField searchTextField;
    private JButton searchBtn;
    private JComboBox queryTypeComboBox;
    private JPanel mainPanel;
    private JList corpusList;
    private DefaultListModel listModel;
    private JButton addCorpusBtn;
    private JButton remCorpusBtn;
    private JButton test_btn;
    private JScrollPane listPane;
    private JScrollPane resultPane;
    private DefaultTableModel tableModel;
    private JTable resultTable;
    private JButton clearBtn;
    private JCheckBox useLiteralCheckBox;
    private JRadioButton literalBtn;
    //private JScrollPane resultPlane = new JScrollPane(resultTable);


    private JFileChooser chooser;

    private String[] queryTypes = {"Transcript", "Metadata"};
    private String choosertitle = "select corpus folder..";
    private int listIndex = 0;

    private ArrayList<File> corpusFolders;
    private static Corpus tbCorpus;
    List<Utterance> results;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SearchWindow();
            }
        });
    }

    public void initialize() {
        frame = new JFrame("Talkbank Search Tool");
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        corpusFolders = new ArrayList<File>();
        listModel = new DefaultListModel();
        corpusList.setModel(listModel);

        tableModel = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                if(column == 1) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        tableModel.addColumn("Speaker");
        tableModel.addColumn("Statement");
        tableModel.addColumn("file_ID");
        tableModel.addColumn("timestamp");
        tableModel.addColumn("Audio");
        resultTable.setModel(tableModel);
        resultTable.getColumnModel().getColumn(1).setCellRenderer(new WordWrapCellRenderer());
        System.out.println(resultTable.toString());

        tbCorpus = new Corpus();
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
                SearchEngine engine = new SearchEngine(tbCorpus);
                results = engine.search(searchTextField.getText(),useLiteralCheckBox.isSelected());
                System.out.println(searchTextField.getText());
                System.out.println(results.size());
                for(Utterance utt : results) {
                    System.out.println(utt.toString());
                }
                String msg = results.size() + " matching utterances have been found";
                /*switch(queryTypeComboBox.getSelectedItem().toString()) {
                    case "Transcript":
                        manager.put("OptionPane.messageForeground", Color.green);
                        msg = "using transcript";
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
                }*/

                fillTable(results,useLiteralCheckBox.isSelected(),searchTextField.getText());
                //System.out.println(resultTable.toString());
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

                    int filesAdded = tbCorpus.putCorpus(chooser.getSelectedFile());
                    System.out.println("Total number of files added to Corpus: "+ filesAdded);
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
        test_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for(Utterance utt : tbCorpus.getUtterances()) {
                    System.out.println(utt.toString());
                }
            }
        });
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                deleteAllRows(tableModel);
            }
        });

        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = resultTable.rowAtPoint(evt.getPoint());
                int col = resultTable.columnAtPoint(evt.getPoint());
                System.out.println("Zappzerapp");
                if (col >= 4) {
                    try {
                        System.out.println(row);
                        results.get(row).playMedia();
                    } catch (FileNotFoundException e) {
                        JOptionPane.showMessageDialog(null,"File not Found");
                        e.printStackTrace();
                    } catch (UnsupportedAudioFileException e) {
                        JOptionPane.showMessageDialog(null,"Audio format not supported");
                        e.printStackTrace();
                    } catch (LineUnavailableException e) {
                        JOptionPane.showMessageDialog(null,"An Error has occured");
                        e.printStackTrace();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null,"Eh..");
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void fillTable(List<Utterance> results,Boolean literal,String query) {

        Object[] row;
        for(Utterance utt : results) {
            if(utt.getStatements().size() == 1) {
                row = new Object[]{utt.getStatements().get(0)};
            }
            List<Statement> statements = utt.getStatements();
            for(Statement s : statements) {
                if(literal && StringUtils.containsIgnoreCase(s.getLiteralStatement(),query)) {
                    row = new Object[]{s.getSpeaker(),s.getLiteralStatement(),utt.getID(),utt.getTimestampAsString(),"play media"};
                    tableModel.addRow(row);
                } else if(StringUtils.containsIgnoreCase(s.getStatement(),query)){
                    row = new Object[]{s.getSpeaker(),s.getStatement(),utt.getID(),utt.getTimestampAsString(),"play media"};
                    tableModel.addRow(row);
                }
            }
        }
    }

    public static void deleteAllRows(final DefaultTableModel model) {
        for( int i = model.getRowCount() - 1; i >= 0; i-- ) {
            model.removeRow(i);
        }
    }

    static class WordWrapCellRenderer extends JTextArea implements TableCellRenderer {
        WordWrapCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            if (table.getRowHeight(row) != getPreferredSize().height) {
                table.setRowHeight(row, getPreferredSize().height);
            }
            return this;
        }
    }

}