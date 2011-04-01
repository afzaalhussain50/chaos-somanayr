package julia;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class JuliaLoader extends JFrame implements ActionListener{

	JPanel imagePane = new JPanel();

	JTextField cR = new JTextField(3);
	JTextField cI = new JTextField(3);

	JTextField save = new JTextField(30);

	JButton build = new JButton("Start rendering");

	JCheckBox saveFractal = new JCheckBox("Save fractal?");

	public JuliaLoader(){
		super("Julia Set Renderer by Ryan Amos");
		setLayout(new BorderLayout());
		JPanel numberPane = new JPanel(new FlowLayout());
		numberPane.add(new JLabel("c value: "));
		numberPane.add(cR);
		numberPane.add(new JLabel(" + "));
		numberPane.add(cI);
		numberPane.add(new JLabel("i"));
		JPanel savePane = new JPanel(new FlowLayout());
		savePane.add(saveFractal);
		savePane.add(new JLabel("Save to: "));
		savePane.add(save);
		JPanel optionPane = new JPanel(new GridLayout(2, 1));
		optionPane.add(numberPane);
		optionPane.add(savePane);
		imagePane.setPreferredSize(new Dimension(500, 500));
		this.add(optionPane, BorderLayout.NORTH);
		this.add(imagePane, BorderLayout.CENTER);
		this.add(build, BorderLayout.SOUTH);
		build.addActionListener(this);
		saveFractal.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				save.setEnabled(saveFractal.isSelected());
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		double r = Double.parseDouble(cR.getText());
		double i = Double.parseDouble(cI.getText());
		JuliaSet js = new JuliaSet(new Complex(r, i), saveFractal.isSelected() ? new Dimension(1000, 1000) : imagePane.getSize());
		if(!saveFractal.isSelected())
			js.render(imagePane.getGraphics());
		else
			try {
				BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
				File f =  new File(save.getText());
				f.getParentFile().mkdirs();
				f.createNewFile();
				js.drawFractal(img.getGraphics());
				ImageIO.write(img, "jpg", f);
				System.out.println("Wrote f(z) = z ^ 2 " + ((r < 0) ? "- " : "+ ") + r + ((i < 0) ? "- " : "+ ") + i + "i to " + f.toString());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	}

	public static void main(String[] args){
		new JuliaLoader().setVisible(true);
	}
}
