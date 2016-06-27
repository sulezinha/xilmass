package gui;

import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 *
 * @author Niels Hulstaert
 */
public class RunDialog extends javax.swing.JDialog {

    /**
     * Creates new form RunDialog.
     *
     * @param parent the parent frame
     * @param modal the modal boolean
     */
    public RunDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        org.apache.log4j.lf5.viewer.LF5SwingUtils.makeVerticalScrollBarTrack(jScrollPane1);
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JButton getClearButton() {
        return clearButton;
    }

    public JTextArea getLogTextArea() {
        return logTextArea;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        cancelButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Xilmass run progress");

        logTextArea.setEditable(false);
        logTextArea.setColumns(20);
        logTextArea.setLineWrap(true);
        logTextArea.setRows(5);
        jScrollPane1.setViewportView(logTextArea);

        cancelButton.setText("cancel");
        cancelButton.setToolTipText("cancel the pipeline execution");
        cancelButton.setMaximumSize(new java.awt.Dimension(85, 27));
        cancelButton.setMinimumSize(new java.awt.Dimension(85, 27));
        cancelButton.setPreferredSize(new java.awt.Dimension(85, 27));

        clearButton.setText("clear");
        clearButton.setToolTipText("clear the log output");
        clearButton.setMaximumSize(new java.awt.Dimension(85, 27));
        clearButton.setMinimumSize(new java.awt.Dimension(85, 27));
        clearButton.setPreferredSize(new java.awt.Dimension(85, 27));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea logTextArea;
    // End of variables declaration//GEN-END:variables
}
