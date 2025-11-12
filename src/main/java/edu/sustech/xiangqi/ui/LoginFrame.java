package edu.sustech.xiangqi.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;


public class LoginFrame {
    public LoginFrame(JFrame frame) {
        initComponents(frame);
    }

    private void initComponents(JFrame frame) {
        JFrame window = new JFrame("Login");
        window.setSize(350, 200);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // --- 创建表单面板 (GridLayout) ---
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5)); 

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
        JPanel buttonPanel = new JPanel(); 
        JButton loginButton = new JButton("登录");
        buttonPanel.add(loginButton);

        // --- 添加登录逻辑 ---
        loginButton.addActionListener(e -> {
            
            // 1. 获取输入
            String username = userField.getText();
            char[] passwordArray = passField.getPassword();
            
            // 2. 转换并比较
            String password = new String(passwordArray);

            if (username.equals("admin") && password.equals("123456")) {
                // 3. 成功: 弹出信息框
                JOptionPane.showMessageDialog(
                    window, 
                    "登录成功! 欢迎, admin!", 
                    "登录结果", 
                    JOptionPane.INFORMATION_MESSAGE
                );
                frame.setVisible(true);
                window.dispose(); 
                
            } else {
                // 4. 失败: 弹出错误框
                JOptionPane.showMessageDialog(
                    window, 
                    "用户名或密码错误。", 
                    "登录失败", 
                    JOptionPane.ERROR_MESSAGE
                );
            }

            // 5. (安全) 清除密码
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

}