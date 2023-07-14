package com.github.druyaned.active_recorder.graphic;

import javax.swing.*;
import javax.swing.table.*;

public class ActiveColumnAdjuster {
    
    public static void adjust(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = table.getTableHeader();
        for (int column = 0; column < table.getColumnCount(); ++column) {
            TableColumn headerColumn = header.getColumnModel().getColumn(column);
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = Math.max(tableColumn.getMinWidth(),
                                          headerColumn.getPreferredWidth());
            int maxWidth = Math.max(tableColumn.getMaxWidth(), headerColumn.getMaxWidth());
            for (int row = 0; row < table.getRowCount(); ++row) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                JComponent c = (JComponent)table.prepareRenderer(renderer, row, column);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);
                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }
            tableColumn.setPreferredWidth(preferredWidth);
        }
    }
    
}
