package com.company.gui;

import com.company.client.Client;
import com.company.client.ResponseInterface;
import com.company.sockets.AuthenticationChecker;
import com.company.sockets.Request;
import com.company.sockets.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by brian on 4/17/16.
 */
public class Application extends JFrame {

    public Application app;
    public class LoginPanel extends JPanel{

        public JTextField username;
        public JTextField password;
        public JButton loginButton;
        public Application app;


        public LoginPanel(Application app){
            this.app = app;
            JPanel container = new JPanel();
            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            username = new JTextField("", 20);
            password = new JTextField("", 20);
            loginButton = new JButton("Login");
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                   app.loginPressed(username.getText(), password.getText());
                }
            });

            JPanel panel0 = new JPanel(new BorderLayout());
            panel0.add(new JLabel("Username"), BorderLayout.NORTH);

            JPanel panel1 = new JPanel(new BorderLayout());
            panel1.add(username, BorderLayout.NORTH);

            JPanel panel15 = new JPanel(new BorderLayout());
            panel15.add(new JLabel("Password"), BorderLayout.NORTH);

            JPanel panel2 = new JPanel(new BorderLayout());
            panel2.add(password, BorderLayout.NORTH);

            JPanel panel3 = new JPanel(new BorderLayout());
            panel3.add(loginButton, BorderLayout.NORTH);

            container.add(panel0);
            container.add(panel1);
            container.add(panel15);
            container.add(panel2);
            container.add(panel3);

            add(container);
        }

    }

    public class EditorPanel extends JPanel{

        public Application app;


        public EditorPanel(Application app) {
            this.app = app;
            this.setLayout(new BorderLayout());
            JPanel leftSide = new JPanel();





            JPanel rightSide = new JPanel();




            add(leftSide, BorderLayout.WEST);
            add(rightSide, BorderLayout.EAST);
        }

        public void refresh(){

        }

    }

    // GUI Elements
    public LoginPanel loginPanel;
    public EditorPanel editorPanel;


    public Request.RequestAuthentication requestAuthentication;

    public Application(){
        this.app = this;
        getContentPane().setLayout(new CardLayout());
        loginPanel = new LoginPanel(this);
        getContentPane().add(loginPanel, "LOGIN_PANEL");
        editorPanel = new EditorPanel(this);
        getContentPane().add(editorPanel, "EDITOR_PANEL");





        setTitle("Server Admin GUI");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }


    // Delegates
    public void loginPressed(String username, String password){
        // Attempt to login
        Request request = new Request(null, new Request.RequestCheckAuthentication(username, AuthenticationChecker.hash(password)));
        new Client(request, new ResponseInterface() {

            Response resp;
            @Override
            public void callback(Response resp) {
                this.resp = resp;
            }

            @Override
            public void run() {
                System.out.println("SOCKETS: Callback called");
                if(this.resp != null && this.resp.requestSuccess){
                    System.out.println("GUI: Login callback successful");
                    app.showMenuEditor();
                    app.loginUser(username, password);
                }else{
                    JOptionPane.showMessageDialog(null, "Whoops! Login failed.");
                }
            }
        }).send();
    }

    public void showMenuEditor(){
        CardLayout cl = (CardLayout)(getContentPane().getLayout());
        cl.show(getContentPane(), "EDITOR_PANEL");
    }

    public void loginUser(String username, String password){
        requestAuthentication = new Request.RequestAuthentication(username, AuthenticationChecker.hash(password));
    }

    public static void main(String[] args){
        new Application();
    }

}
