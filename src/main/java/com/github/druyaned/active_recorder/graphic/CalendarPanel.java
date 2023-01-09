package com.github.druyaned.active_recorder.graphic;

import static com.github.druyaned.active_recorder.active.ActiveTime.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import com.github.druyaned.active_recorder.active.*;
import java.time.Month;

public class CalendarPanel extends JPanel {
    public static final int W = AppFrame.W;
    public static final int H = AppFrame.H - ControlPanel.H;
    public static final int COLUMNS = DAYS_IN_WEEK;
    public static final int ROWS = MAX_DAY / COLUMNS + 2;
    public static final Color FILLER_COLOR = new Color(204, 204, 204);
    
    public final String YEARS_PANEL_NAME = "Active Years";
    public final String MONTHS_PANEL_NAME = "Active Months";
    public final String DAYS_PANEL_NAME = "Active Days";
    
    private static final Paint gradient;
    
    static {
        float x1 = 0, y1 = 0;
        Color c1 = Color.WHITE;
        float x2 = W, y2 = H;
        Color c2 = new Color(240, 255, 255);
        gradient = new GradientPaint(x1, y1, c1, x2, y2, c2);
    }
    
//-Non-static---------------------------------------------------------------------------------------
    
    private final ActiveCalendar calendar;
    private final YearsPanel yearsPanel;
    private final MonthsPanel monthsPanel;
    private final DaysPanel daysPanel;
    private final JLabel dateDisplay;
    
    private String currentCardPanelName;
    final CardLayout cardsLayout;
    final JPanel cardsPanel;

    public CalendarPanel(ActiveCalendar calendar) {
        setPreferredSize(new Dimension(W, H));
        this.calendar = calendar;

        cardsLayout = new CardLayout();
        cardsPanel = new JPanel(cardsLayout); // years, months, days
        cardsPanel.setOpaque(false);

        // days pane
        DayButtonTasks dayButtonTasks = new DayButtonTasks();
        daysPanel = new DaysPanel(calendar, dayButtonTasks);
        
        // months pane
        MonthButtonTasks monthButtonTasks = new MonthButtonTasks(daysPanel,
            () -> setCurrentCardPanelName(DAYS_PANEL_NAME),
            () -> setDateDisplayForDaysPanel(),
            () -> cardsLayout.show(cardsPanel, DAYS_PANEL_NAME)
        );
        monthsPanel = new MonthsPanel(calendar, monthButtonTasks);

        // years pane
        YearButtonTasks yearButtonTasks = new YearButtonTasks(monthsPanel,
            () -> setCurrentCardPanelName(MONTHS_PANEL_NAME),
            () -> setDateDisplayForMonthsPanel(),
            () -> cardsLayout.show(cardsPanel, MONTHS_PANEL_NAME)
        );
        yearsPanel = new YearsPanel(calendar, yearButtonTasks);

        // add created panes to the cards pane
        cardsPanel.add(yearsPanel, YEARS_PANEL_NAME);
        cardsPanel.add(monthsPanel, MONTHS_PANEL_NAME);
        cardsPanel.add(daysPanel, DAYS_PANEL_NAME);

        // back-button
        final JButton backButton = new JButton("<");
        backButton.addActionListener((e) -> {
            if (getCurrentCardPanelName().equals(DAYS_PANEL_NAME)) {
                setCurrentCardPanelName(MONTHS_PANEL_NAME);
                setDateDisplayForMonthsPanel();
                cardsLayout.show(cardsPanel, MONTHS_PANEL_NAME);
            } else if (getCurrentCardPanelName().equals(MONTHS_PANEL_NAME)) {
                setCurrentCardPanelName(YEARS_PANEL_NAME);
                setDateDisplayForYearsPanel();
                cardsLayout.show(cardsPanel, YEARS_PANEL_NAME);
            }
        });

        // date display
        dateDisplay = new JLabel();
        dateDisplay.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
        dateDisplay.setHorizontalAlignment(JLabel.CENTER);
        Color color = new Color(64, 128, 128);
        Border border = BorderFactory.createLineBorder(color, 1, true);
        dateDisplay.setBorder(border);
        currentCardPanelName = DAYS_PANEL_NAME;
        setDateDisplayForDaysPanel();
        cardsLayout.show(cardsPanel, DAYS_PANEL_NAME);
        
        // manage panel
        JPanel managePanel = new JPanel(new BorderLayout());
        managePanel.setOpaque(false);
        managePanel.add(backButton, BorderLayout.WEST);
        managePanel.add(dateDisplay, BorderLayout.CENTER);

        // add manage and cards pane
        setLayout(new BorderLayout());
        add(managePanel, BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);
    }

    public void setDayFrameInvisible() { daysPanel.setDayFrameInvisible(); }

    public void dayFrameDispose() { daysPanel.dayFrameDispose(); }

//-Private-methods----------------------------------------------------------------------------------

    private String getCurrentCardPanelName() { return currentCardPanelName; }

    private void setCurrentCardPanelName(String cardPanelName) {
        currentCardPanelName = cardPanelName;
    }

    private void setDateDisplayForYearsPanel() { dateDisplay.setText("Active years"); }

    private void setDateDisplayForMonthsPanel() {
        dateDisplay.setText(Integer.toString(monthsPanel.getCurrentYear()));
    }

    private void setDateDisplayForDaysPanel() {
        int monthNumber = daysPanel.getCurrentMonth();
        String monthName = Month.of(monthNumber).toString();
        String f = "%d/%d (%s)";
        dateDisplay.setText(String.format(f, daysPanel.getCurrentYear(), monthNumber, monthName));
    }

//-Methods------------------------------------------------------------------------------------------

    public void take(Activity a) {
        calendar.add(a);
        yearsPanel.updateBy(calendar);
        monthsPanel.updateBy(calendar);
        daysPanel.updateBy(calendar);
        switch (getCurrentCardPanelName()) {
            case DAYS_PANEL_NAME -> setDateDisplayForDaysPanel();
            case MONTHS_PANEL_NAME -> setDateDisplayForMonthsPanel();
            default -> setDateDisplayForYearsPanel();
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}
