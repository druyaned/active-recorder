package com.github.druyaned.active_recorder.graphic;

import static com.github.druyaned.active_recorder.active.Activity.MAX_DESCR_LENGTH;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.*;
import com.github.druyaned.active_recorder.active.*;
import com.github.druyaned.active_recorder.data.*;
import java.time.Instant;

public class ControlPanel extends JPanel {
    public static final int W = AppFrame.W;
    public static final int H = AppFrame.H / 3;
    public static final String STOPWATCH_FORMAT = "%d:%02d:%02d";
    private static final Font DESCR_LABEL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    private static final Font DESCR_FIELD_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    private static final Paint gradient;

    static {
        float x1 = 0, y1 = H;
        Color c1 = Color.WHITE;
        float x2 = W, y2 = H;
        Color c2 = new Color(240, 255, 255);
        gradient = new GradientPaint(x1, y1, c1, x2, y2, c2);
    }

//-Non-static---------------------------------------------------------------------------------------

    private ActiveMode mode = null;
    private final ButtonGroup modeChooser;
    private final JTextField descrField;
    private final Stopwatch stopwatch;

    /**
     * A control panel of the app which consists of:
     *  <p><ol>
     *      <li>mode chooser;</li>
     *      <li>description text field;</li>
     *      <li>begin and end buttons of the {@link Stopwatch stopwatch};</li>
     *      <li>stopwatch display.</li>
     *  </ol><p>
     * Provides the {@code taker} with necessary info.
     * 
     * @param taker takes info from the control pane.
     * @param data the {@link Data app data} to read-write.
     * @throws IOException in same cases as {@link StartFileReader#read}
     */
    public ControlPanel(Consumer<Activity> taker, Data data) throws IOException {
        setPreferredSize(new Dimension(W, H));
        String fontName = Font.SANS_SERIF;
        int fontStyle = Font.PLAIN;
        int modeFontSize = 14;
        int stopwatchButtonFontSize = 20;
        int stopwatchFontSize = 24;

        // initializing all control components
        final JRadioButton development = new JRadioButton("Development");
        final JRadioButton stagnation = new JRadioButton("Stagnation");
        final JRadioButton relaxation = new JRadioButton("Relaxation");
        descrField = new JTextField("");
        final JButton begin = new JButton("BEGIN");
        final JButton end = new JButton("END");
        final JLabel stopwatchDisplay = new JLabel(String.format(STOPWATCH_FORMAT, 0, 0, 0));

        // adding all begin-end tasks
        StopwatchTasks stopwatchTasks = new StopwatchTasks(stopwatchDisplay);
        stopwatchTasks.addBeginTask(() -> begin.setEnabled(false), "begin.disable");
        stopwatchTasks.addBeginTask(() -> end.setEnabled(true), "end.enable");
        stopwatchTasks.addEndTask(() -> begin.setEnabled(false), "begin.disable");
        stopwatchTasks.addEndTask(() -> end.setEnabled(false), "end.disable");
        stopwatchTasks.addEndTask(this::clearModeSelection, "clearModeSelection");
        stopwatch = new Stopwatch(stopwatchTasks);

        // begin and end buttons
        begin.setBackground(new Color(96, 255, 96));
        end.setBackground(new Color(255, 96, 96));
        begin.setFont(new Font(fontName, Font.BOLD, stopwatchButtonFontSize));
        end.setFont(new Font(fontName, Font.BOLD, stopwatchButtonFontSize));
        begin.addActionListener((e) -> stopwatch.start());
        end.addActionListener((e) -> {
            final String m = "Can't add a new activity.";
            final String t = "Inability to add";
            final int messageType = JOptionPane.INFORMATION_MESSAGE;
            try {
                Activity a = stopwatch.stop(getMode(), descrField.getText());
                if (a != null) {
                    taker.accept(a);
                } else {
                    JOptionPane.showMessageDialog(getParent(), m, t, messageType);
                }
            } catch (IllegalArgumentException exc) {
                JOptionPane.showMessageDialog(getParent(), m, t, messageType);
            }
        });
        
        // stopwatch display
        stopwatchDisplay.setFont(new Font(fontName, fontStyle, stopwatchFontSize));
        begin.setEnabled(false);
        if (data.startFile.EMPTY_AT_INITIALIZATION) {
            end.setEnabled(false);
        } else {
            StartData startData = StartFileReader.read(data.startFile);
            if (startData != null) {
                mode = startData.mode;
                if (mode == ActiveMode.DEVELOPMENT) {
                    development.setSelected(true);
                } else if (mode == ActiveMode.STAGNATION) {
                    stagnation.setSelected(true);
                } else if (mode == ActiveMode.RELAXATION) {
                    relaxation.setSelected(true);
                }
                descrField.setText(startData.descr);
                stopwatch.startFrom(startData.time);
                end.setEnabled(true);
            } else {
                end.setEnabled(false);
            }
            
        }

        // description area
        descrField.setFont(DESCR_FIELD_FONT);
        descrField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String text = descrField.getText();
                if (text.length() >= MAX_DESCR_LENGTH + 1) {
                    e.consume();
                    String shorter = text.substring(0, MAX_DESCR_LENGTH);
                    descrField.setText(shorter);
                } else if (text.length() >= MAX_DESCR_LENGTH)  {
                    e.consume();
                }
            }
        });
        JLabel descrLabel = new JLabel("Description:");
        descrLabel.setHorizontalAlignment(JLabel.RIGHT);
        descrLabel.setFont(DESCR_LABEL_FONT);
        JPanel descrPanel = new JPanel(new GridLayout(1, 2));
        descrPanel.setOpaque(false);
        descrPanel.add(descrLabel);
        descrPanel.add(descrField);

        // button group to choose active mode
        modeChooser = new ButtonGroup();
        modeChooser.add(development);
        modeChooser.add(stagnation);
        modeChooser.add(relaxation);
        development.setFont(new Font(fontName, fontStyle, modeFontSize));
        stagnation.setFont(new Font(fontName, fontStyle, modeFontSize));
        relaxation.setFont(new Font(fontName, fontStyle, modeFontSize));
        ActionListener modeListener = (event) -> {
            if (development.isSelected()) {
                mode = ActiveMode.DEVELOPMENT;
            } else if (stagnation.isSelected()) {
                mode = ActiveMode.STAGNATION;
            } else if (relaxation.isSelected()) {
                mode = ActiveMode.RELAXATION;
            }
            if (!end.isEnabled()) {
                begin.setEnabled(true);
            }
        };
        development.addActionListener(modeListener);
        stagnation.addActionListener(modeListener);
        relaxation.addActionListener(modeListener);
        JPanel modePanel = new JPanel();
        modePanel.setOpaque(false);
        modePanel.add(development);
        modePanel.add(stagnation);
        modePanel.add(relaxation);
        JPanel stopwatchButtonsPanel = new JPanel();
        stopwatchButtonsPanel.setOpaque(false);
        stopwatchButtonsPanel.add(begin);
        stopwatchButtonsPanel.add(end);
        
        // empty components for the correct positioning
        int topFillerWidth = W / 8;
        int topFillerHeight = H / 8;
        int sideFillerWidth = W / 8;
        int sideFillerHeight = W - topFillerHeight;
        EmptyComp topFiller = new EmptyComp(topFillerWidth, topFillerHeight);
        EmptyComp leftFiller = new EmptyComp(sideFillerWidth, sideFillerHeight);
        EmptyComp rightFiller = new EmptyComp(sideFillerWidth, sideFillerHeight);

        // add all elements in a correct position
        setLayout(new GridBagLayout());
        GBC modeGBC = new GBC(1, 1, 1, 1);
        GBC descrGBC = new GBC(1, 2, 1, 1).setFill(GBC.HORIZONTAL);
        GBC stopwatchButtonsGBC = new GBC(1, 3, 1, 1);
        GBC displayGBC = new GBC(1, 4, 1, 1);
        GBC topFillerGBC = new GBC(0, 0, 3, 1);
        GBC leftFillerGBC = new GBC(0, 1, 1, 3);
        GBC rightFillerGBC = new GBC(2, 1, 1, 3);
        add(modePanel, modeGBC);
        add(descrPanel, descrGBC);
        add(stopwatchButtonsPanel, stopwatchButtonsGBC);
        add(stopwatchDisplay, displayGBC);
        add(topFiller, topFillerGBC);
        add(leftFiller, leftFillerGBC);
        add(rightFiller, rightFillerGBC);
    }

//-Getters------------------------------------------------------------------------------------------

    public ActiveMode getMode() { return mode; }
    
    public String getDescription() { return descrField.getText(); }
    
    public Instant getStartTime() { return stopwatch.getStartDateTime(); }
    
    public boolean isStopwatchStarted() { return stopwatch.started(); }

//-Methods------------------------------------------------------------------------------------------

    private void clearModeSelection() {
        modeChooser.clearSelection();
        mode = null;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
