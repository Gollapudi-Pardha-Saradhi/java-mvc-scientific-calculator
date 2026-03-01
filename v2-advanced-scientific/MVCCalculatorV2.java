v2-advanced-scientific/MVCCalculatorV2.java
  import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
class CalculatorModel{
    public double evaluate(String expression){
        return evaluateExpression(expression.replaceAll("\\s+",""));
    }
    private double evaluateExpression(String exp){
        return parseExpression(new Tokenizer(exp));
    }
    private double parseExpression(Tokenizer tk){
        double value=parseTerm(tk);
        while(tk.hasNext()){
            char op=tk.peek();
            if(op=='+'||op=='-'){
                tk.next();
                double next=parseTerm(tk);
                value=(op=='+')?value+next:value-next;
            } else break;
        }
        return value;
    }
    private double parseTerm(Tokenizer tk){
        double value=parseFactor(tk);
        while(tk.hasNext()){
            char op=tk.peek();
            if(op=='*'||op=='/'){
                tk.next();
                double next=parseFactor(tk);
                value=(op=='*')?value*next:value/next;
            } else break;
        }
        return value;
    }
    private double parseFactor(Tokenizer tk){
        double value=parsePower(tk);
        while(tk.hasNext()&&tk.peek()=='%'){
            tk.next();
            value=value/100.0;
        }
        return value;
    }
    private double parsePower(Tokenizer tk){
        double value=parseUnary(tk);
        while (tk.hasNext()&&tk.peek()=='^') {
            tk.next();
            double exponent=parseUnary(tk);
            value=Math.pow(value,exponent);
        }
        return value;
    }
    private double parseUnary(Tokenizer tk){
        if(tk.hasNext()&&tk.peek()=='+'){
            tk.next();
            return parseUnary(tk);
        }
        if(tk.hasNext()&&tk.peek()=='-'){
            tk.next();
            return -parseUnary(tk);
        }
        return parsePrimary(tk);
    }
    private double parsePrimary(Tokenizer tk){
        if(tk.peek()=='(') {
            tk.next();
            double value=parseExpression(tk);
            tk.next();
            return value;
        }
        if(Character.isLetter(tk.peek())){
            String func=tk.readFunction();
            tk.next();
            double value=parseExpression(tk);
            tk.next();
            return applyFunction(func,value);
        }
        double number=tk.readNumber();
        if(tk.hasNext()&&tk.peek()=='!'){
            tk.next();
            return factorial((int)number);
        }
        return number;
    }
    private double applyFunction(String func,double value){
        switch(func){
            case "sin":return Math.sin(Math.toRadians(value));
            case "cos":return Math.cos(Math.toRadians(value));
            case "tan":return Math.tan(Math.toRadians(value));
            case "log":return Math.log10(value);
            case "ln":return Math.log(value);
            case "sqrt":return Math.sqrt(value);
            default:throw new RuntimeException("Unknown function");
        }
    }
    private double factorial(int n){
        if(n<0) throw new RuntimeException("Negative factorial");
        double result=1;
        for(int i=2;i<=n;i++)
            result*=i;
        return result;
    }
}
class Tokenizer{
    private final String input;
    private int pos=0;
    Tokenizer(String input){
        this.input=input;
    }
    boolean hasNext(){
        return pos<input.length();
    }
    char peek(){
        return input.charAt(pos);
    }
    void next(){
        pos++;
    }
    double readNumber(){
        int start=pos;
        while(hasNext()&&(Character.isDigit(peek())||peek()=='.'))
            pos++;
        return Double.parseDouble(input.substring(start,pos));
    }
    String readFunction(){
        int start=pos;
        while(hasNext()&&Character.isLetter(peek()))
            pos++;
        return input.substring(start,pos);
    }
}
class CalculatorView extends JFrame{
    JTextField display=new JTextField();
    JTextArea historyArea=new JTextArea();
    JButton themeToggle=new JButton("Switch Theme");
    JButton clearHistoryBtn=new JButton("Clear History");
    boolean darkMode=true;
    JPanel mainPanel=new JPanel(new BorderLayout());
    JPanel buttonPanel=new JPanel(new GridLayout(7,4,5,5));
    JPanel bottomPanel=new JPanel(new GridLayout(1,2));
    CalculatorView(){
        setTitle("Scientific Calculator");
        setSize(750,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        display.setFont(new Font("Arial",Font.BOLD,24));
        mainPanel.add(display,BorderLayout.NORTH);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Arial",Font.PLAIN,16));
        JScrollPane scroll=new JScrollPane(historyArea);
        scroll.setPreferredSize(new Dimension(250,600));
        mainPanel.add(scroll,BorderLayout.EAST);
        String[] buttons={
                "7","8","9","/",
                "4","5","6","*",
                "1","2","3","-",
                "0",".","(",")",
                "sin","cos","tan","^",
                "log","ln","sqrt","!",
                "%","C","DEL","="
        };
        for(String text:buttons){
            JButton btn=new JButton(text);
            btn.setFont(new Font("Arial",Font.BOLD,18));
            buttonPanel.add(btn);
            btn.addActionListener(e->{
                if(text.equals("="))
                    display.postActionEvent();
                else if(text.equals("C"))
                    display.setText("");
                else if(text.equals("DEL")){
                    String t=display.getText();
                    if(!t.isEmpty())
                        display.setText(t.substring(0,t.length()-1));
                }
                else display.setText(display.getText()+text);
            });
        }
        mainPanel.add(buttonPanel,BorderLayout.CENTER);
        themeToggle.setFont(new Font("Arial",Font.BOLD,14));
        clearHistoryBtn.setFont(new Font("Arial",Font.BOLD,14));
        bottomPanel.add(themeToggle);
        bottomPanel.add(clearHistoryBtn);
        mainPanel.add(bottomPanel,BorderLayout.SOUTH);
        add(mainPanel);
        applyDarkTheme();
        themeToggle.addActionListener(e->{
            darkMode=!darkMode;
            if(darkMode) applyDarkTheme();
            else applyLightTheme();
        });
        clearHistoryBtn.addActionListener(e->{
            historyArea.setText("");
        });
    }
    void applyDarkTheme(){
        mainPanel.setBackground(new Color(30,30,30));
        buttonPanel.setBackground(new Color(30,30,30));
        bottomPanel.setBackground(new Color(30,30,30));
        display.setBackground(new Color(45,45,45));
        display.setForeground(Color.WHITE);
        historyArea.setBackground(new Color(40,40,40));
        historyArea.setForeground(Color.WHITE);
        for(Component c:buttonPanel.getComponents()){
            c.setBackground(new Color(60,60,60));
            c.setForeground(Color.WHITE);
        }
        themeToggle.setBackground(new Color(70,70,70));
        themeToggle.setForeground(Color.WHITE);
        clearHistoryBtn.setBackground(new Color(120,40,40));
        clearHistoryBtn.setForeground(Color.WHITE);
    }
    void applyLightTheme(){
        mainPanel.setBackground(Color.WHITE);
        buttonPanel.setBackground(Color.WHITE);
        bottomPanel.setBackground(Color.WHITE);
        display.setBackground(Color.WHITE);
        display.setForeground(Color.BLACK);
        historyArea.setBackground(Color.WHITE);
        historyArea.setForeground(Color.BLACK);
        for(Component c:buttonPanel.getComponents()){
            c.setBackground(Color.LIGHT_GRAY);
            c.setForeground(Color.BLACK);
        }
        themeToggle.setBackground(Color.LIGHT_GRAY);
        themeToggle.setForeground(Color.BLACK);
        clearHistoryBtn.setBackground(Color.PINK);
        clearHistoryBtn.setForeground(Color.BLACK);
    }
}
class CalculatorController{
    CalculatorModel model;
    CalculatorView view;
    CalculatorController(CalculatorModel m,CalculatorView v){
        model=m;
        view=v;
        view.display.addActionListener(e->calculate());
        view.display.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode()==KeyEvent.VK_ESCAPE)
                    view.display.setText("");
            }
        });
    }
    void calculate(){
        try{
            String exp=view.display.getText();
            double result=model.evaluate(exp);
            view.historyArea.append(exp+" = "+result+"\n");
            view.display.setText(String.valueOf(result));
        }catch(Exception ex){
            view.display.setText("Error");
        }
    }
}
public class MVCCalculatorV2{
    public static void main(String[] args){
        SwingUtilities.invokeLater(()->{
            CalculatorModel model=new CalculatorModel();
            CalculatorView view=new CalculatorView();
            new CalculatorController(model,view);
            view.setVisible(true);
        });
    }
}
