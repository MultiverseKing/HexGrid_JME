package org.hexgridapi.editor.hexmap.gui;

import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;

/**
 *
 * @author roah
 */
public class NewMapPan extends javax.swing.JPanel {

    /**
     * Creates new form NewMapPan
     */
    public NewMapPan() {
        initComponents();
    }

    public boolean getUseBuffer() {
        return useBufferBox.isSelected();
    }

    public int getBufferRadius() {
        return chunkSizeSlider.getValue();
    }

    public boolean getUseProcedural() {
        return useProceduralBox.isSelected();
    }

    public boolean getBuildVoid() {
        return buildVoidBox.isSelected();
    }

    public boolean getUseOnlyGround() {
        return onlyGroundBox.isSelected();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jFileChooser1 = new javax.swing.JFileChooser();
        jToggleButton1 = new javax.swing.JToggleButton();
        javax.swing.JLabel mapNameLabel = new javax.swing.JLabel();
        mapNameField = new javax.swing.JTextField();
        coordinateChooser = new javax.swing.JComboBox();
        javax.swing.JLabel chunkSizeLabel = new javax.swing.JLabel();
        chunkSizeSlider = new javax.swing.JSlider();
        textureChooser = new javax.swing.JComboBox();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        bufferRadius = new javax.swing.JSlider();
        javax.swing.JLabel bufferRadiusLabel = new javax.swing.JLabel();
        javax.swing.JLabel radiusValue = new javax.swing.JLabel();
        useBufferBox = new javax.swing.JCheckBox();
        onlyGroundBox = new javax.swing.JCheckBox();
        javax.swing.JLabel chunkCoordinateLabel = new javax.swing.JLabel();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        useProceduralBox = new javax.swing.JCheckBox();
        proceduralChooser = new javax.swing.JComboBox();
        javax.swing.JLabel chunkSizeValue = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        buildVoidBox = new javax.swing.JCheckBox();
        jComboBox1 = new javax.swing.JComboBox();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();

        jToggleButton1.setText("jToggleButton1");

        setMinimumSize(new java.awt.Dimension(400, 250));

        mapNameLabel.setText("Map Name :");

        mapNameField.setText("NewMap");

        coordinateChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SquareCoordinate", "HexagonCoordinate", "Custom..." }));
        coordinateChooser.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chooserItemStateChanged(evt);
            }
        });

        chunkSizeLabel.setText("Chunk Size : ");

        chunkSizeSlider.setMajorTickSpacing(1);
        chunkSizeSlider.setMaximum(5);
        chunkSizeSlider.setMinimum(1);
        chunkSizeSlider.setPaintTicks(true);
        chunkSizeSlider.setSnapToTicks(true);
        chunkSizeSlider.setValue(3);

        textureChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DEFAULT", "EARTH", "ICE", "NATURE", "VOLT" }));

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        bufferRadius.setMajorTickSpacing(1);
        bufferRadius.setMaximum(3);
        bufferRadius.setMinorTickSpacing(1);
        bufferRadius.setPaintTicks(true);
        bufferRadius.setSnapToTicks(true);
        bufferRadius.setValue(1);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, useBufferBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), bufferRadius, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        bufferRadiusLabel.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        bufferRadiusLabel.setText("Buffer Radius : ");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, bufferRadius, org.jdesktop.beansbinding.ELProperty.create("${value}"), radiusValue, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        useBufferBox.setSelected(true);
        useBufferBox.setText("Use Buffer");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bufferRadius, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(bufferRadiusLabel)
                                .addGap(18, 18, 18)
                                .addComponent(radiusValue))
                            .addComponent(useBufferBox))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(useBufferBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bufferRadiusLabel)
                    .addComponent(radiusValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bufferRadius, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        onlyGroundBox.setText("Only Ground");

        chunkCoordinateLabel.setText("Chunk Coordinate :");

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        useProceduralBox.setText("Use Procedural");

        proceduralChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DEFAULT", "CUSTOM..." }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, useProceduralBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), proceduralChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        proceduralChooser.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chooserItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(useProceduralBox)
                        .addGap(0, 41, Short.MAX_VALUE))
                    .addComponent(proceduralChooser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(useProceduralBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(proceduralChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, chunkSizeSlider, org.jdesktop.beansbinding.ELProperty.create("${value}"), chunkSizeValue, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel3.setText("Main Texture :");

        buildVoidBox.setSelected(true);
        buildVoidBox.setText("Build void tile");
        buildVoidBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildVoidBoxActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "WATER", "SKY&CLOUD (todo...)", "UNDERGROUND (todo...)", "SPACE (todo...)", "CUSTOM..." }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chooserItemStateChanged(evt);
            }
        });

        jLabel1.setText("Void Area Type :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(mapNameLabel)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(textureChooser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mapNameField)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buildVoidBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chunkSizeLabel)
                        .addGap(18, 18, 18)
                        .addComponent(chunkSizeValue))
                    .addComponent(chunkCoordinateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(onlyGroundBox)
                    .addComponent(chunkSizeSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(coordinateChooser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mapNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mapNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textureChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buildVoidBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chunkCoordinateLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(coordinateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(onlyGroundBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chunkSizeLabel)
                            .addComponent(chunkSizeValue))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chunkSizeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
    
    private void chooserItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chooserItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED
                && evt.getItem().toString().toUpperCase().equals("CUSTOM...")) {
            System.err.println("@todo");
        }
    }//GEN-LAST:event_chooserItemStateChanged

    private void buildVoidBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildVoidBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buildVoidBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider bufferRadius;
    private javax.swing.JCheckBox buildVoidBox;
    private javax.swing.JSlider chunkSizeSlider;
    private javax.swing.JComboBox coordinateChooser;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JTextField mapNameField;
    private javax.swing.JCheckBox onlyGroundBox;
    private javax.swing.JComboBox proceduralChooser;
    private javax.swing.JComboBox textureChooser;
    private javax.swing.JCheckBox useBufferBox;
    private javax.swing.JCheckBox useProceduralBox;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}