import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import java.util.Collections;
import javax.swing.text.BadLocationException;

public class TypingTutor extends JFrame implements KeyListener{
    private static final String[] KEYS = {
            "~", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "+", "Backspace",
            "Tab", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\",
            "Caps", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'", "↑", "Enter",
            "Shift", "Z", "X", "C", "V", "B", "N", "M", ",", ".", "?", "←", "↓", "→"};

    private JPanel keysPanel;
    private JTextArea textArea;

    // 初始化
    public TypingTutor(){
        super("Touch Typing");

        // 建立虛擬鍵盤
        keysPanel = new JPanel(new GridLayout(4, 15, 1, 1));
        keysPanel.setBackground(new java.awt.Color(249, 241, 241));
        for(String key : KEYS){
            JButton button = new JButton(key);
            button.setPreferredSize(new Dimension(60, 60));
            button.setBorder(new LineBorder(Color.WHITE, 1));
            button.setBackground(new java.awt.Color(196, 191, 223));
            button.addKeyListener(this);
            keysPanel.add(button);
        }

        // 建立文字區域
        textArea = new JTextArea(5, 30);
        textArea.setBackground(new java.awt.Color(249, 241, 241));
        textArea.setFont(new Font("SERIF", Font.PLAIN, 20));

        // 版面調整
        add(keysPanel, BorderLayout.SOUTH);
        add(textArea, BorderLayout.CENTER);

        // 其他設定
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setVisible(true);

        // 定義TAB
        // 禁用默認TAB的行為
        keysPanel.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
        keysPanel.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.emptySet());
        // 自定義
        String tabKey = "tabKey";
        InputMap inputMap = keysPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), tabKey);
        ActionMap actionMap = keysPanel.getActionMap();
        actionMap.put(tabKey, new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                // 不用做任何事
            }
        });
    }

    // 按下按鍵
    public void keyPressed(KeyEvent e){
        char key = e.getKeyChar();
        JButton button = getButton(Character.toUpperCase(key), e);
        if(button != null){
            button.setBackground(new java.awt.Color(245, 246, 141));
        }
    }

    // 鬆開按鍵
    public void keyReleased(KeyEvent e){
        char key = e.getKeyChar();
        JButton button = getButton(Character.toUpperCase(key), e);
        if(button != null){
            button.setBackground(new java.awt.Color(196, 191, 223));
            // Caps lock、Shift
            if(e.getKeyCode() == KeyEvent.VK_CAPS_LOCK || e.getKeyCode() == KeyEvent.VK_SHIFT){
                // 不用做任何事
            }
            // Backspace
            else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                int caretPosition = textArea.getCaretPosition();
                if(caretPosition <= textArea.getDocument().getLength()){
                    try{
                        textArea.getDocument().remove(caretPosition - 1, 1);
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
            // Up、Left、Down、Right
            else if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_RIGHT){
                try{
                    int keyCode = e.getKeyCode();
                    int caretPosition = textArea.getCaretPosition();
                    int line = textArea.getLineOfOffset(caretPosition);
                    int column = caretPosition - textArea.getLineStartOffset(line);
        
                    // 判斷不同case
                    switch(keyCode){
                        case KeyEvent.VK_UP:
                            if(line > 0){
                                int newCaretPosition = textArea.getLineStartOffset(line - 1) + Math.min(column, textArea.getLineEndOffset(line - 1) - textArea.getLineStartOffset(line - 1) - 1);
                                textArea.setCaretPosition(newCaretPosition);
                            }
                            break;
                        case KeyEvent.VK_DOWN:
                            if(line < textArea.getLineCount() - 1){
                                int newCaretPosition = textArea.getLineStartOffset(line + 1) + Math.min(column, textArea.getLineEndOffset(line + 1) - textArea.getLineStartOffset(line + 1));
                                textArea.setCaretPosition(newCaretPosition);
                            }
                            break;
                        case KeyEvent.VK_LEFT:
                            if(caretPosition > 0){
                                textArea.setCaretPosition(caretPosition - 1);
                            }
                            break;
                        case KeyEvent.VK_RIGHT:
                            if(caretPosition < textArea.getText().length()){
                                textArea.setCaretPosition(caretPosition + 1);
                            }
                            break;
                    }
                }
                catch(BadLocationException ex){
                    ex.printStackTrace();
                }
            }
            // 其他
            else{
                textArea.insert(Character.toString(key), textArea.getCaretPosition());
            }
        }
    }

    // 輸入字符
    public void keyTyped(KeyEvent e){
        // 不用做任何事
    }

    // 查找對應按鍵
    private JButton getButton(char key, KeyEvent e){
        // Backspace
        if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
            return (JButton) keysPanel.getComponent(13);
        }
        // Tab
        if(e.getKeyCode() == KeyEvent.VK_TAB){
            return (JButton) keysPanel.getComponent(14);
        }
        // Caps lock
        if(e.getKeyCode() == KeyEvent.VK_CAPS_LOCK){
            return (JButton) keysPanel.getComponent(28);
        }
        // Enter
        if(key == "\n".charAt(0)){
            return (JButton) keysPanel.getComponent(41);
        }
        // Shift
        if(e.getKeyCode() == KeyEvent.VK_SHIFT){
            return (JButton) keysPanel.getComponent(42);
        }
        // Up
        if(e.getKeyCode() == KeyEvent.VK_UP){
            return (JButton) keysPanel.getComponent(40);
        }
        // Left
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            return (JButton) keysPanel.getComponent(53);
        }
        // Down
        if(e.getKeyCode() == KeyEvent.VK_DOWN){
            return (JButton) keysPanel.getComponent(54);
        }
        // Right
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            return (JButton) keysPanel.getComponent(55);
        }
        // 其他
        for(int i = 0; i < keysPanel.getComponentCount(); i++){
            // 跳過Backspace、Tab、Caps lock
            if(i == 13 || i == 14 || i == 28){
                continue;
            }

            JButton button = (JButton) keysPanel.getComponent(i);
            if(button.getText().charAt(0) == key){
                return button;
            }
        }
        return null;
    }

    public static void main(String[] args){
        new TypingTutor();
    }
}