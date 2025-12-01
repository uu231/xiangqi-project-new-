package edu.sustech.xiangqi.ui;

import edu.sustech.xiangqi.storage.User;
import edu.sustech.xiangqi.storage.UserManager;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;


public class LoginFrame {
    private JFrame window;
    private final Runnable onLoginSuccess;

    public LoginFrame(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
        initComponents();
    }

    private void initComponents() {
        JFrame window = new JFrame("Login");
        window.setSize(350, 200);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // --- 创建表单面板 (GridLayout) ---
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5)); 
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,40,20,40));
        // 3. 创建组件
        JLabel userLabel = new JLabel(" 用户名:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel(" 密码:");
        JPasswordField passField = new JPasswordField(15);

        formPanel.add(userLabel);   
        formPanel.add(userField);   
        formPanel.add(passLabel);   
        formPanel.add(passField);   

        // --- 创建按钮面板 (FlowLayout) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        JButton loginButton = new JButton("登录");
        JButton registerButton = new JButton("注册");
        styleButton(loginButton);
        styleButton(registerButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // --- 添加登录逻辑 ---
        loginButton.addActionListener(e -> {
            
            // 1. 获取输入
            String username = userField.getText();
            char[] passwordArray = passField.getPassword();
            String password =new String(passwordArray);
            if(username.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(window,"用户名或密码不能为空！","提示",JOptionPane.WARNING_MESSAGE);
            return;
            }

           User user = UserManager.login(username,password);
            if(user != null){
                JOptionPane.showMessageDialog(window,"欢迎回来，"+user.getUsername()+"!","登录成功",JOptionPane.INFORMATION_MESSAGE);
                if(onLoginSuccess!=null){
                    onLoginSuccess.run();
                }
                window.dispose();
            }else{
                if(UserManager.userExists(username)){
                    JOptionPane.showMessageDialog(window,"密码错误，请重试。","登录失败",JOptionPane.ERROR_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(window,"用户不存在，请先注册","登录失败",JOptionPane.ERROR_MESSAGE);
                }
            }

            // 5. (安全) 清除密码
            Arrays.fill(passwordArray, '0');
        });
        registerButton.addActionListener(e -> {
            String username = userField.getText().trim();
            char[] passwordArray = passField.getPassword();
            String password =new String(passwordArray);

            if(username.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(window,"用户名或密码不能为空！","提示",JOptionPane.WARNING_MESSAGE);
                return;}
            if(UserManager.registerUser(username,password)){
                JOptionPane.showMessageDialog(window,"注册成功，请点击登录。","成功",JOptionPane.INFORMATION_MESSAGE);

            }else{
                if(UserManager.userExists(username)){
                    JOptionPane.showMessageDialog(window,"用户名已存在，请直接登录。","注册失败",JOptionPane.ERROR_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(window,"注册失败，发生未知错误，请重试。","错误",JOptionPane.ERROR_MESSAGE);
                }
            }
            Arrays.fill(passwordArray, '0');
        });
        // --- 组合 ---
        window.add(formPanel, BorderLayout.CENTER);
        window.add(buttonPanel, BorderLayout.SOUTH);
        //  居中显示窗口
        window.setLocationRelativeTo(null); 
        // 6. 显示窗口
        window.setVisible(true);
    }
    private void styleButton(JButton button){
        button.setFocusPainted(false);
        button.setFont(new Font("微软雅黑",Font.PLAIN,14));
        button.setPreferredSize(new Dimension(80,30));
    }

}