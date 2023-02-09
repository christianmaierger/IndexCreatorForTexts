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
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel filePathLabel = new JLabel("Path to TXT/Word File:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        inputPanel.add(filePathLabel, constraints);

        JTextField filePathField = new JTextField();
        filePathField.setPreferredSize(new Dimension(200, filePathField.getPreferredSize().height));
        constraints.gridx = 1;
        constraints.gridy = 0;
        inputPanel.add(filePathField, constraints);

        JButton browseButton = new JButton("Browse");
        constraints.gridx = 2;
        constraints.gridy = 0;
        inputPanel.add(browseButton, constraints);

        JLabel granularityLabel = new JLabel("Granularity:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        inputPanel.add(granularityLabel, constraints);

        JComboBox<String> granularityComboBox = new JComboBox<>(new String[]{"Line", "Verse"});
        constraints.gridx = 1;
        constraints.gridy = 1;
        inputPanel.add(granularityComboBox, constraints);

        JLabel dgapLabel = new JLabel("Calculate Dgaps (distance between terms):");
        constraints.gridx = 0;
        constraints.gridy = 2;
        inputPanel.add(dgapLabel, constraints);

        JCheckBox dgapCheckBox = new JCheckBox();
        constraints.gridx = 1;
        constraints.gridy = 2;
        inputPanel.add(dgapCheckBox, constraints);

        JLabel linesLabel = new JLabel("Remove Empty Lines:");
        constraints.gridx = 0;
        constraints.gridy = 3;
        inputPanel.add(linesLabel, constraints);

        JCheckBox linesCheckBox = new JCheckBox();
        constraints.gridx = 1;
        constraints.gridy = 3;
        inputPanel.add(linesCheckBox, constraints);
        add(inputPanel, BorderLayout.CENTER);

        granularityComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String granularity = (String) granularityComboBox.getSelectedItem();
                if (granularity.equals("Line")) {
                    linesCheckBox.setVisible(true);
                    linesLabel.setVisible(true);
                } else {
                    linesCheckBox.setVisible(false);
                    linesLabel.setVisible(false);
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
                printService.printParametersAkaKenngroessen(granularity);
                printService.printInvertedList();
                JOptionPane.showMessageDialog(this, "Index created successfully");
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage());
            }



        });
        add(submitButton, BorderLayout.SOUTH);

        setVisible(true);
    }




    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        window.setVisible(true);
    }
}
