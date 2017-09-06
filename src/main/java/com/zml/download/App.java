package com.zml.download;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import com.alee.laf.WebLookAndFeel;

/**
 * Hello world!
 *
 */
public class App implements ActionListener
{
    public static void main( String[] args ) throws Exception
    {
    	javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
            	WebLookAndFeel.install ();
                new App().createAndShowGUI();
            }
        });
    	//new App().multpleThreadDownload();
    }

    public void multpleThreadDownload() throws Exception {
    	HttpURLConnection conn = getConnection();

    	int length = conn.getContentLength();
    	conn.disconnect();

    	RandomAccessFile targetFile = new RandomAccessFile("temp.apk", "rwd");
    	targetFile.setLength(length);
    	targetFile.close();

    	 int blockSize = length / 5;
         for (int threadId = 1; threadId <= 5; threadId++) {
             int startIndex = (threadId - 1) * blockSize;
             int endIndex = startIndex + blockSize - 1;
             if (threadId == 5) {
                 endIndex = length;
             }
             System.out.println("线程" + threadId + "下载:" + startIndex + "字节~" + endIndex + "字节");
             new DownloadThread(threadId, startIndex, endIndex).start();
         }
    }

    public HttpURLConnection getConnection() throws Exception {
    	URL url = new URL("http://file.ws.126.net/3g/client/netease_newsreader_android.apk");
    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	conn.setReadTimeout(5 * 1000);
    	conn.setRequestMethod("GET");
    	conn.setRequestProperty("connection", "keep-alive");
    	conn.setRequestProperty("accept", "*/*");
    	return conn;
    }

    public class DownloadThread extends Thread {
    	private int id;
    	private int startIndex;
    	private int endIndex;
    	private int downloadSize;
    	private int currentIndex;

		public DownloadThread(int id, int startIndex, int endIndex) {
			this.id = id;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		@Override
		public void run() {
			try {
				HttpURLConnection conn = getConnection();
				conn.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);

				 InputStream in = conn.getInputStream();
	             RandomAccessFile remoteFile = new RandomAccessFile("temp.apk", "rwd");
	             remoteFile.seek(startIndex);


                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                	this.downloadSize += len;
                	remoteFile.write(buffer, 0, len);
                }
                in.close();
                remoteFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

    private void createAndShowGUI() {
    	JFrame frame = new JFrame("HelloWorldSwing");
    	Container contentPane = frame.getContentPane();

    	JPanel mainPan=new JPanel(new BorderLayout());
    	mainPan.setPreferredSize(new Dimension(300,500));

        contentPane.add(mainPan);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JToolBar toolBar = createToolBar();

        JPanel panel = new JPanel();
        BoxLayout layout=new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);
        JScrollPane scrollPane = new JScrollPane(panel);
        JPanel taskPanel = new JPanel();
        taskPanel.setPreferredSize(new Dimension(300,70));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(50);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(200, 15));
        taskPanel.add(progressBar);
        panel.add(taskPanel);
        panel.add(new JSeparator(JSeparator.HORIZONTAL),
                BorderLayout.LINE_START);

        JPanel taskPanel2 = new JPanel();
        taskPanel2.setPreferredSize(new Dimension(300,70));
        panel.add(taskPanel2);

        panel.add(new JSeparator(JSeparator.HORIZONTAL),
                BorderLayout.LINE_START);

        JPanel taskPanel3 = new JPanel();
        taskPanel3.setPreferredSize(new Dimension(300,70));
        panel.add(taskPanel3);

        panel.add(new JSeparator(JSeparator.HORIZONTAL),
                BorderLayout.LINE_START);

        JPanel taskPanel4 = new JPanel();
        taskPanel4.setPreferredSize(new Dimension(300,70));
        panel.add(taskPanel4);

        panel.add(new JSeparator(JSeparator.HORIZONTAL),
                BorderLayout.LINE_START);

        JPanel taskPanel5 = new JPanel();
        taskPanel5.setPreferredSize(new Dimension(300,70));
        panel.add(taskPanel5);

        //Lay out the main panel.
    	mainPan.add(toolBar, BorderLayout.PAGE_START);
    	mainPan.add(scrollPane, BorderLayout.CENTER);
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        String imgLocation = "images/newTask.gif";
        URL imageURL = App.class.getResource(imgLocation);
        JButton button = new JButton();
        button.setActionCommand("NEW");
        button.setToolTipText("New a download task.");
        button.addActionListener(this);

        if (imageURL != null) {
        	button.setIcon(new ImageIcon(imageURL, "New Task"));
        } else {
        	button.setText("New Task");
        	System.err.println("Resource not found: " + imgLocation);
        }
        toolBar.add(button);
        return toolBar;
    }

    private void createTaskPanel() {

    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}


}
