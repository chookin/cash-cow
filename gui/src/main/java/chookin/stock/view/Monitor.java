/*
 * Created by JFormDesigner on Thu Aug 14 21:22:46 CST 2014
 */

package chookin.stock.view;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import chookin.stock.model.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.beansbinding.*;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

/**
 * @author chookin
 */
public class Monitor extends JDialog {
    public Monitor(Frame owner) {
        super(owner);
        initComponents();
    }

    public Monitor(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("gui");
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menuItem1 = new JMenuItem();
        panelQuery = new JPanel();
        textFieldQuery = new JTextField();
        buttonQurey = new JButton();
        buttonAdd = new JButton();
        panelView = new JPanel();
        panelStocks = new JPanel();
        panelConfig = new JPanel();
        buttonSet = new JButton();
        buttonMessages = new JButton();
        buttonNews = new JButton();
        buttonTags = new JButton();
        buttonRank = new JButton();
        scrollPaneTable = new JScrollPane();
        tableStock = new JTable();
        stockQuery1 = new StockQuery();
        stockChoosed1 = new StockChoosed();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "default:grow",
            "default, fill:default:grow"));

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText(bundle.getString("Monitor.menu1.text"));

                //---- menuItem1 ----
                menuItem1.setText(bundle.getString("Monitor.menuItem1.text"));
                menu1.add(menuItem1);
            }
            menuBar1.add(menu1);
        }
        setJMenuBar(menuBar1);

        //======== panelQuery ========
        {
            panelQuery.setLayout(new FormLayout(
                "78dlu, 2*($lcgap, default)",
                "default"));
            panelQuery.add(textFieldQuery, CC.xy(1, 1));

            //---- buttonQurey ----
            buttonQurey.setText(bundle.getString("Monitor.buttonQurey.text"));
            panelQuery.add(buttonQurey, CC.xy(3, 1));

            //---- buttonAdd ----
            buttonAdd.setText(bundle.getString("Monitor.buttonAdd.text"));
            panelQuery.add(buttonAdd, CC.xy(5, 1));
        }
        contentPane.add(panelQuery, CC.xy(1, 1));

        //======== panelView ========
        {
            panelView.setLayout(new FormLayout(
                "177dlu, $lcgap, default:grow",
                "fill:default:grow"));

            //======== panelStocks ========
            {
                panelStocks.setLayout(new FormLayout(
                    "default:grow",
                    "default, $lgap, fill:default:grow"));

                //======== panelConfig ========
                {
                    panelConfig.setLayout(new FormLayout(
                        "4*(default), default:grow",
                        "default"));

                    //---- buttonSet ----
                    buttonSet.setIcon(new ImageIcon(getClass().getResource("icons/set.gif")));
                    panelConfig.add(buttonSet, CC.xy(1, 1));

                    //---- buttonMessages ----
                    buttonMessages.setText(bundle.getString("Monitor.buttonMessages.text"));
                    panelConfig.add(buttonMessages, CC.xy(2, 1));

                    //---- buttonNews ----
                    buttonNews.setText(bundle.getString("Monitor.buttonNews.text"));
                    panelConfig.add(buttonNews, CC.xy(3, 1));

                    //---- buttonTags ----
                    buttonTags.setText(bundle.getString("Monitor.buttonTags.text"));
                    panelConfig.add(buttonTags, CC.xy(4, 1));

                    //---- buttonRank ----
                    buttonRank.setText(bundle.getString("Monitor.buttonRank.text"));
                    panelConfig.add(buttonRank, CC.xy(5, 1));
                }
                panelStocks.add(panelConfig, CC.xy(1, 1));

                //======== scrollPaneTable ========
                {

                    //---- tableStock ----
                    tableStock.setModel(new DefaultTableModel(
                        new Object[][] {
                            {"stock id", "price", "change"},
                            {null, null, null},
                        },
                        new String[] {
                            "stock id", "price", "change"
                        }
                    ));
                    scrollPaneTable.setViewportView(tableStock);
                }
                panelStocks.add(scrollPaneTable, CC.xy(1, 3));
            }
            panelView.add(panelStocks, CC.xy(1, 1));
        }
        contentPane.add(panelView, CC.xy(1, 2));
        pack();
        setLocationRelativeTo(getOwner());

        //---- bindings ----
        bindingGroup = new BindingGroup();
        bindingGroup.addBinding(Bindings.createAutoBinding(UpdateStrategy.READ_WRITE,
            stockQuery1, BeanProperty.create("stock"),
            textFieldQuery, BeanProperty.create("text")));
        bindingGroup.bind();
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuItem1;
    private JPanel panelQuery;
    private JTextField textFieldQuery;
    private JButton buttonQurey;
    private JButton buttonAdd;
    private JPanel panelView;
    private JPanel panelStocks;
    private JPanel panelConfig;
    private JButton buttonSet;
    private JButton buttonMessages;
    private JButton buttonNews;
    private JButton buttonTags;
    private JButton buttonRank;
    private JScrollPane scrollPaneTable;
    private JTable tableStock;
    private StockQuery stockQuery1;
    private StockChoosed stockChoosed1;
    private BindingGroup bindingGroup;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
