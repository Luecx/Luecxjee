package projects.interpolator;import luecx.ai.neuralnetwork.Network;import luecx.ai.neuralnetwork.NetworkBuilder;import luecx.ai.neuralnetwork.activation.Sigmoid;import luecx.ai.neuralnetwork.activation.TanH;import luecx.ai.neuralnetwork.layers.DenseLayer;import luecx.math.values.tensor.Vector;import luecx.visual.basic.framework.Frame;import luecx.visual.basic.framework.Panel;import javax.swing.*;import java.awt.*;public class InterpolationPanel extends Panel {    public InterpolationPanel() {        initComponents();    }    private void initComponents() {        jTextField1 = new javax.swing.JTextField();        jButton1 = new javax.swing.JButton();        jButton2 = new javax.swing.JButton();        jPanel1 = new javax.swing.JPanel();        networkPanel = new NetworkOutputPanel(this);        jSlider1 = new javax.swing.JSlider();        jScrollPane2 = new javax.swing.JScrollPane();        jList1 = new javax.swing.JList<>();        jTextField1.setText("3, 10, 10, 1");        jButton1.setText("start");        jButton1.addActionListener(new java.awt.event.ActionListener() {            public void actionPerformed(java.awt.event.ActionEvent evt) {                jButton1ActionPerformed(evt);            }        });        jButton2.setText("stop");        jButton2.addActionListener(new java.awt.event.ActionListener() {            public void actionPerformed(java.awt.event.ActionEvent evt) {                jButton2ActionPerformed(evt);            }        });        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Content"));        jPanel1.setLayout(new java.awt.BorderLayout());        networkPanel.addMouseListener(new java.awt.event.MouseAdapter() {            public void mousePressed(java.awt.event.MouseEvent evt) {                networkPanelMousePressed(evt);            }        });        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(networkPanel);        networkPanel.setLayout(jPanel2Layout);        jPanel2Layout.setHorizontalGroup(                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)                        .addGap(0, 481, Short.MAX_VALUE)        );        jPanel2Layout.setVerticalGroup(                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)                        .addGap(0, 496, Short.MAX_VALUE)        );        jPanel1.add(networkPanel, java.awt.BorderLayout.CENTER);        jSlider1.setMajorTickSpacing(10);        jSlider1.setMinorTickSpacing(1);        jSlider1.setOrientation(javax.swing.JSlider.VERTICAL);        jSlider1.setPaintLabels(true);        jSlider1.setPaintTicks(true);        jSlider1.setSnapToTicks(true);        jList1.setModel(new javax.swing.AbstractListModel<String>() {            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };            public int getSize() { return strings.length; }            public String getElementAt(int i) { return strings[i]; }        });        jScrollPane2.setViewportView(jList1);        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);        this.setLayout(layout);        layout.setHorizontalGroup(                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)                        .addGroup(layout.createSequentialGroup()                                .addContainerGap()                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)                                        .addGroup(layout.createSequentialGroup()                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)                                                .addComponent(jButton1)                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)                                                .addComponent(jButton2))                                        .addGroup(layout.createSequentialGroup()                                                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)                                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)))                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)                                .addContainerGap())        );        layout.setVerticalGroup(                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)                        .addGroup(layout.createSequentialGroup()                                .addContainerGap()                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)                                        .addComponent(jScrollPane2)                                        .addGroup(layout.createSequentialGroup()                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)                                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)                                                        .addComponent(jTextField1)                                                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)                                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)                                                        .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))                                .addContainerGap())        );        jList1.setModel(new DefaultListModel<String>());        this.interpolation = new BooleanInterpolation(3, this);        this.networkPanel.setInterpolation(this.interpolation);    }    private BooleanInterpolation interpolation;    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {        if(interpolation.getNetwork() == null){            NetworkBuilder builder = new NetworkBuilder(1,1,3);            String[] ar = jTextField1.getText().replace(" ", "").split(",");            for(int i = 1; i < ar.length; i++){                builder.addLayer(new DenseLayer(Integer.parseInt(ar[i]))                        .setActivationFunction((i != ar.length - 1) ? new TanH(): new Sigmoid()));            }            Network net = builder.buildNetwork();            this.interpolation.setNetwork(net);            System.out.println(interpolation.getNetwork() + "  --" );        }        interpolation.startTraining();    }    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {        if(interpolation.getNetwork() == null){        }else{            interpolation.stopTraining();        }    }    private double round(double v){        return (int)(100 * v) / 100d;    }    private void networkPanelMousePressed(java.awt.event.MouseEvent evt) {        if(evt.getButton() == 2){            interpolation.createRandomPoints(10);        }else{            double x = round(evt.getX() / (double) jPanel1.getWidth());            double y = round(evt.getY() / (double) jPanel1.getHeight());            double z = this.jSlider1.getValue() / (double)100;            boolean v = evt.getButton() == 1? true:false;            interpolation.addPoint(new Vector(x,y,z), v);            ((DefaultListModel<String>) jList1.getModel()).addElement("["+x+","+y+","+z+"]="+v);        }    }    private javax.swing.JButton jButton1;    private javax.swing.JButton jButton2;    private javax.swing.JList<String> jList1;    private javax.swing.JPanel jPanel1;    private NetworkOutputPanel networkPanel;    private javax.swing.JScrollPane jScrollPane2;    private javax.swing.JSlider jSlider1;    private javax.swing.JTextField jTextField1;    public JSlider getjSlider1() {        return jSlider1;    }    @Override    public void drawContent(Graphics2D graphics2D) {        graphics2D.clearRect(0,0,10000,10000);    }    @Override    public void update() {        if(this.networkPanel != null)            this.networkPanel.repaint();    }    public static void main(String[] args) {        new Frame(new InterpolationPanel());    }}