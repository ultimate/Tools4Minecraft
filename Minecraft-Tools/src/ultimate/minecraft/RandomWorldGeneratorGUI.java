package ultimate.minecraft;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import ultimate.minecraft.seeds.ExistingMapsSeedGenerator;
import ultimate.minecraft.seeds.SeedGenerator;
import ultimate.minecraft.seeds.UrbanDictionarySeedGenerator;
import ultimate.minecraft.seeds.WikipediaSeedGenerator;

public class RandomWorldGeneratorGUI extends JFrame implements WindowListener
{
	private static final long				serialVersionUID	= 1L;

	private static final GeneratorLabel[]	generatorClasses	= new GeneratorLabel[] { new GeneratorLabel(WikipediaSeedGenerator.class),
			new GeneratorLabel(UrbanDictionarySeedGenerator.class), new GeneratorLabel(ExistingMapsSeedGenerator.class) };

	private int								worldSize;
	private File							toolsDir;
	private File							outputDir;
	private boolean							removeWorlds;
	private RandomWorldGenerator			generator;
	private RandomWorldGeneratorThread		thread;

	private JLabel							sizeLabel;
	private JLabel							toolsLabel;
	private JLabel							outputLabel;
	private JLabel							generatorLabel;
	private JLabel							optionsLabel;
	private JLabel							statusLabel;

	private JSpinner						sizeSpinner;
	private FileChooser						toolsFileChooser;
	private FileChooser						outputFileChooser;
	private JComboBox<GeneratorLabel>		generatorComboBox;
	private JCheckBox						removeCheckBox;
	private JButton							startButton;
	private JButton							stopButton;

	public RandomWorldGeneratorGUI()
	{
		sizeLabel = new JLabel("World Size:");
		sizeLabel.setSize(120, 20);
		sizeLabel.setLocation(10, 10);

		toolsLabel = new JLabel("Tools-Directory:");
		toolsLabel.setSize(120, 20);
		toolsLabel.setLocation(10, 40);

		outputLabel = new JLabel("Output-Directory:");
		outputLabel.setSize(120, 20);
		outputLabel.setLocation(10, 70);

		generatorLabel = new JLabel("Seed-Generator:");
		generatorLabel.setSize(120, 20);
		generatorLabel.setLocation(10, 100);
		
		optionsLabel = new JLabel("Options:");
		optionsLabel.setSize(120, 20);
		optionsLabel.setLocation(10, 130);

		statusLabel = new JLabel("");
		statusLabel.setSize(280, 20);
		statusLabel.setLocation(10, 230);

		sizeSpinner = new JSpinner(new SpinnerNumberModel(2000, 100, 10000, 100));
		sizeSpinner.setSize(250, 20);
		sizeSpinner.setLocation(140, 10);

		toolsFileChooser = new FileChooser();
		toolsFileChooser.setSize(250, 20);
		toolsFileChooser.setLocation(140, 40);

		outputFileChooser = new FileChooser();
		outputFileChooser.setSize(250, 20);
		outputFileChooser.setLocation(140, 70);

		generatorComboBox = new JComboBox<GeneratorLabel>(generatorClasses);
		generatorComboBox.setSize(250, 20);
		generatorComboBox.setLocation(140, 100);
		
		removeCheckBox = new JCheckBox("Remove Worlds after Generation");
		removeCheckBox.setSize(250, 20);
		removeCheckBox.setLocation(140, 130);

		startButton = new JButton();
		startButton.setText("START");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				worldSize = (Integer) sizeSpinner.getValue();
				toolsDir = toolsFileChooser.getFile();
				if(toolsDir == null || toolsDir.getName().equals(""))
				{
					error("invalid tools-directory!");
					return;
				}
				outputDir = outputFileChooser.getFile();
				if(outputDir == null || outputDir.getName().equals(""))
				{
					error("invalid output-directory!");
					return;
				}
				removeWorlds = removeCheckBox.isSelected();
				try
				{
					generator = new RandomWorldGenerator(((GeneratorLabel) generatorComboBox.getSelectedItem()).generatorClass, toolsDir, outputDir,
							worldSize, removeWorlds);
				}
				catch(Exception e)
				{
					error(e.getMessage());
					e.printStackTrace();
					return;
				}
				startButton.setEnabled(false);
				thread = new RandomWorldGeneratorThread(generator) {
					@Override
					public void printStatus(int i)
					{
						super.printStatus(i);
						status("Generating world #" + i);
					}
				};
				thread.start();
				stopButton.setEnabled(true);
			}
		});
		startButton.setLocation(90, 200);
		startButton.setSize(80, 20);

		stopButton = new JButton();
		stopButton.setText("STOP");
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				stopButton.setEnabled(false);
				try
				{
					thread.stopGeneration();
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				thread = null;
				startButton.setEnabled(true);
			}
		});
		stopButton.setLocation(210, 200);
		stopButton.setSize(80, 20);

		this.getContentPane().setLayout(null);// new GridLayout(4, 2));

		this.getContentPane().add(sizeLabel, null);
		this.getContentPane().add(sizeSpinner, null);
		this.getContentPane().add(toolsLabel, null);
		this.getContentPane().add(toolsFileChooser, null);
		this.getContentPane().add(outputLabel, null);
		this.getContentPane().add(outputFileChooser, null);
		this.getContentPane().add(generatorLabel, null);
		this.getContentPane().add(generatorComboBox, null);
		this.getContentPane().add(optionsLabel, null);
		this.getContentPane().add(removeCheckBox, null);
		this.getContentPane().add(startButton, null);
		this.getContentPane().add(stopButton, null);
		this.getContentPane().add(statusLabel, null);

		this.repaint();

		this.setVisible(true);
		this.setSize(420, 300);
		this.addWindowListener(this);
	}

	private void error(String e)
	{
		statusLabel.setText("Error: " + e);
	}

	private void status(String s)
	{
		statusLabel.setText(s);
	}

	private class FileChooser extends JPanel
	{
		private static final long	serialVersionUID	= 1L;
		private JFileChooser		fileChooser;
		private JTextField			textField;
		private JButton				button;

		public FileChooser()
		{
			fileChooser = new JFileChooser(new File("."));
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			textField = new JTextField(20);
			textField.setEditable(false);
			textField.setSize(230, 20);
			textField.setLocation(0, 0);

			button = new JButton();
			button.setText("...");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					if(fileChooser.showOpenDialog(RandomWorldGeneratorGUI.this) == JFileChooser.APPROVE_OPTION)
					{
						textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
					}
				}
			});
			button.setSize(20, 20);
			button.setLocation(230, 0);

			this.setLayout(null);
			this.add(textField);
			this.add(button);
		}

		public File getFile()
		{
			return new File(textField.getText());
		}
	}

	private static class GeneratorLabel
	{
		private Class<? extends SeedGenerator>	generatorClass;

		public GeneratorLabel(Class<? extends SeedGenerator> generatorClass)
		{
			super();
			this.generatorClass = generatorClass;
		}

		public String toString()
		{
			return generatorClass.getSimpleName();
		}
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		if(thread != null)
		{
			System.out.println("Waiting for current generation to finish...");
			try
			{
				thread.stopGeneration();
			}
			catch(InterruptedException e1)
			{
				e1.printStackTrace();
				System.exit(1);
			}
		}
		System.exit(0);
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		RandomWorldGeneratorGUI gui = new RandomWorldGeneratorGUI();
		gui.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
