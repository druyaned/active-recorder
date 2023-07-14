package com.github.druyaned.active_recorder.graphic;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class ActiveTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        ActiveTableModel model = (ActiveTableModel) table.getModel();
        final JComponent component = (JComponent)super
                .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Color color = new Color(128, 128, 128);
        int top = 0, left = 0, right = 1, bottom = 1;
        Border border = BorderFactory.createMatteBorder(top, left, bottom, right, color);
        component.setBorder(border);
        component.setBackground(model.getRowColor(row));
        return component;
    }
    
}
