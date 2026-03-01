v1-basic-mvc/MVCCalculatorV1.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
class CalculatorModel{
    public double evaluate(String expression){
        return evaluateExpression(expression);
    }
    private double evaluateExpression(String exp){
        Stack<Double> values=new Stack<>();
        Stack<Character> ops=new Stack<>();
        for (int i=0;i<exp.length();i++){
            char ch=exp.charAt(i);
            if(Character.isWhitespace(ch))
                continue;
            if(Character.isDigit(ch)||ch=='.'){
                StringBuilder sb=new StringBuilder();
                while(i<exp.length()&&(Character.isDigit(exp.charAt(i))||exp.charAt(i)=='.')){
                    sb.append(exp.charAt(i++));
                }
                i--;
                values.push(Double.parseDouble(sb.toString()));
            }
            else if(ch=='('){
                ops.push(ch);
            }
            else if(ch==')'){
                while(ops.peek()!='(')
                    values.push(applyOp(ops.pop(),values.pop(),values.pop()));
                ops.pop();
            }
            else if(isOperator(ch)){
                while (!ops.isEmpty()&&precedence(ch)<=precedence(ops.peek()))
                    values.push(applyOp(ops.pop(),values.pop(),values.pop()));
                ops.push(ch);
            }
        }
        while(!ops.isEmpty())
            values.push(applyOp(ops.pop(),values.pop(),values.pop()));
        return values.pop();
    }
    private boolean isOperator(char ch){
        return ch=='+'||ch=='-'||ch=='*'||ch=='/';
    }
    private int precedence(char op){
        if(op=='+'||op=='-')
            return 1;
        if(op=='*'||op=='/')
            return 2;
        return 0;
    }
    private double applyOp(char op,double b,double a) {
        switch(op){
            case '+':return a + b;
            case '-':return a - b;
            case '*':return a * b;
            case '/':
                if(b == 0)
                    throw new ArithmeticException("Divide by zero");
                return a / b;
        }
        return 0;
    }
}
class CalculatorView extends JFrame{
    JTextField display=new JTextField();
    JTextArea historyArea=new JTextArea();
    JButton[] buttons;
    String[] btnLabels={
            "7","8","9","/",
            "4","5","6","*",
            "1","2","3","-",
            "0",".","=","+",
            "(",")","C","DEL"
    };
    public CalculatorView(){
        setTitle("MVC Scientific Calculator");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        display.setFont(new Font("Arial", Font.BOLD, 28));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.BLACK);
        display.setForeground(Color.GREEN);
        add(display,BorderLayout.NORTH);
        JPanel centerPanel=new JPanel(new GridLayout(5,4,10,10));
        centerPanel.setBackground(Color.DARK_GRAY);
        buttons=new JButton[btnLabels.length];
        for(int i=0;i<btnLabels.length;i++){
            buttons[i]=new JButton(btnLabels[i]);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 20));
            buttons[i].setBackground(Color.GRAY);
            buttons[i].setForeground(Color.WHITE);
            centerPanel.add(buttons[i]);
        }
        add(centerPanel,BorderLayout.CENTER);
        historyArea.setEditable(false);
        historyArea.setBackground(Color.BLACK);
        historyArea.setForeground(Color.WHITE);
        JScrollPane scrollPane=new JScrollPane(historyArea);
        scrollPane.setPreferredSize(new Dimension(500,150));
        add(scrollPane,BorderLayout.SOUTH);
        setVisible(true);
    }
}
class CalculatorController implements ActionListener{
    private CalculatorModel model;
    private CalculatorView view;
    public CalculatorController(CalculatorModel model,CalculatorView view){
        this.model=model;
        this.view=view;
        for(JButton button:view.buttons)
            button.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        String command=e.getActionCommand();
        try {
            if(command.matches("[0-9.]")){
                view.display.setText(view.display.getText()+command);
            }
            else if(command.equals("C")){
                view.display.setText("");
            }
            else if(command.equals("DEL")){
                String text=view.display.getText();
                if(!text.isEmpty())
                    view.display.setText(text.substring(0,text.length()-1));
            }
            else if(command.equals("=")){
                String expression=view.display.getText();
                double result=model.evaluate(expression);
                view.historyArea.append(expression + " = " + result + "\n");
                view.display.setText(String.valueOf(result));
            }
            else{
                view.display.setText(view.display.getText()+command);
            }
        }
        catch(Exception ex){
            view.display.setText("Error");
        }
    }
}
public class MVCCalculator{
    public static void main(String[] args){
        CalculatorModel model=new CalculatorModel();
        CalculatorView view=new CalculatorView();
        new CalculatorController(model,view);
    }
}
