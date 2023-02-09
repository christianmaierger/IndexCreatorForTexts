import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * The Main class is the entry point for the application. It handles input from the user to retrieve the
 * file to be indexed and the granularity of the documents. The result is printed to the console in the form
 * of a list of parameters and an inverted list.
 *
 * @author Christian Maier
 */
public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Index Creator");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));


        JLabel filePathLabel = new JLabel("Path to TXT/Word File:");
        JTextField filePathField = new JTextField();
        JButton browseButton = new JButton("Browse");
        inputPanel.add(filePathLabel);
        inputPanel.add(filePathField);
        inputPanel.add(browseButton);

        JLabel granularityLabel = new JLabel("Granularity:");
        JComboBox<String> granularityComboBox = new JComboBox<>(new String[]{"line", "verse"});
        inputPanel.add(granularityLabel);
        inputPanel.add(granularityComboBox);
        // empty placeholder to move dgapLabel in same row to dgapCheckBox
        JComponent j = new JComponent() {};
        inputPanel.add(j);

        JLabel dgapLabel = new JLabel("Include dgap:");
        JCheckBox dgapCheckBox = new JCheckBox();
        inputPanel.add(dgapLabel);
        inputPanel.add(dgapCheckBox);

        JComponent j2 = new JComponent() {};
        inputPanel.add(j2);

        JLabel linesLabel = new JLabel("Remove Empty Lines:");
        JCheckBox linesCheckBox = new JCheckBox();
        inputPanel.add(linesLabel);
        inputPanel.add(linesCheckBox);

        add(inputPanel, BorderLayout.CENTER);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnValue = chooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            String granularity = (String) granularityComboBox.getSelectedItem();
            boolean includeDgap = dgapCheckBox.isSelected();
            boolean removeLines = linesCheckBox.isSelected();
            Path file = Paths.get(filePath);
            try {

            IndexCreatorService indexCreator = new IndexCreatorService(file, granularity);
            Index resultIndex = indexCreator.createIndexForDocument(removeLines,includeDgap);

            PrintService printService = new PrintService(resultIndex);
            printService.printParametersAkaKenngroeßen(granularity);
            printService.printInvertedList();
            JOptionPane.showMessageDialog(this, "Index created successfully");
            } catch (Exception exception) {
               JOptionPane.showMessageDialog(this, exception.getMessage());
               System.out.println("jetzt schauen wir mal");
            }



        });
        add(submitButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
