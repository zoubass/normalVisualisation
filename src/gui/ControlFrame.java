package gui;

import renderer.Renderer;

import javax.swing.*;
import java.awt.*;

public class ControlFrame extends JFrame {

	private Renderer renderer;

	public ControlFrame(Renderer renderer) {
		this.renderer = renderer;

		JFrame frame = new JFrame("ControlPanel");
		frame.setSize(300, 500);

		JPanel panel = new JPanel();
		panel.setSize(200, 400);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gridBag = new GridBagConstraints();

		JComboBox shapesComboBox = new JComboBox(
				new Object[] { "Koule", "Kužel", "Válec", "Plocha", "Diabolo", "Hadr", "Fazole" });

		shapesComboBox.addActionListener(e -> {
			Object item = shapesComboBox.getSelectedItem();

			if (item == "Koule") {
				renderer.setShape(0);
			}
			if (item == "Kužel") {
				renderer.setShape(1);
			}
			if (item == "Válec") {
				renderer.setShape(2);
			}
			if (item == "Plocha") {
				renderer.setShape(3);
			}
			if (item == "Diabolo") {
				renderer.setShape(4);
			}
			if (item == "Hadr") {
				renderer.setShape(5);
			}
			if (item == "Fazole") {
				renderer.setShape(6);
			}
		});

		gridBag.gridx = 0;
		gridBag.gridy = 0;
		gridBag.gridwidth = 4;
		shapesComboBox.setSelectedItem("Hadr");
		panel.add(shapesComboBox, gridBag);

		gridBag.ipady = 0;
		gridBag.gridx = 0;
		gridBag.gridwidth = 2;
		gridBag.gridy = 1;
		createReflectorCheckBox(panel, "Reflector", gridBag);

		gridBag.gridx = 0;
		gridBag.gridy = 2;
		gridBag.ipady = 0;
		gridBag.gridwidth = 2;
		createAttenuationCheckBox(panel, "Osvětlení s útlumem", gridBag);

		JRadioButton normalMappingRadio = new JRadioButton();
		JRadioButton paralaxMappingRadio = new JRadioButton();

		normalMappingRadio.setSelected(true);
		normalMappingRadio.addChangeListener(changeEvent -> {

			if (normalMappingRadio.isSelected()) {
				paralaxMappingRadio.setSelected(false);
				renderer.setTextureMap(0);
			}
		});

		paralaxMappingRadio.addChangeListener(changeEvent -> {

			if (paralaxMappingRadio.isSelected()) {
				normalMappingRadio.setSelected(false);
				renderer.setTextureMap(1);
			}
		});

		gridBag.ipady = 0;
		gridBag.gridx = 0;
		gridBag.gridwidth = 2;
		gridBag.gridy = 5;
		panel.add(new Label("normal tex. mapping"), gridBag);
		gridBag.gridx = 2;
		panel.add(normalMappingRadio, gridBag);

		gridBag.ipady = 0;
		gridBag.gridx = 0;
		gridBag.gridwidth = 2;
		gridBag.gridy = 6;
		panel.add(new Label("paralax text. mapping"), gridBag);
		gridBag.gridx = 2;
		panel.add(paralaxMappingRadio, gridBag);

		JComboBox texturesComboBox = new JComboBox(
				new Object[] { "Bricks SN", "Normals", "Vertices", "Just Blue-ish" });

		texturesComboBox.addActionListener(e -> {
			Object item = texturesComboBox.getSelectedItem();

			if (item == "Bricks SN") {
				renderer.setTextureType(0);
			}
			if (item == "Normals") {
				renderer.setTextureType(1);
			}
			if (item == "Vertices") {
				renderer.setTextureType(2);
			}
			if (item == "Just Blue-ish") {
				renderer.setTextureType(3);
			}
		});

		texturesComboBox.setSelectedItem("Just Blue-ish");

		gridBag.ipady = 0;
		gridBag.gridx = 0;
		gridBag.gridwidth = 2;
		gridBag.gridy = 7;
		panel.add(new Label("Texture"), gridBag);
		gridBag.gridx = 2;
		panel.add(texturesComboBox, gridBag);

		gridBag.ipady = 0;
		gridBag.gridx = 0;
		gridBag.gridwidth = 2;
		gridBag.gridy = 8;
		createTransparencyCheckbox(panel, "Discard", gridBag);

		gridBag.ipady = 0;
		gridBag.gridx = 0;
		gridBag.gridy = 9;
		gridBag.gridwidth = 5;
		Label sectionHeader = new Label("KONFIGURACE TÝKAJÍCÍ SE NORMÁL ");
		sectionHeader.setFont(Font.getFont(Font.SANS_SERIF));

		panel.add(sectionHeader, gridBag);

		gridBag.ipady = 0;
		gridBag.gridx = 0;
		gridBag.gridwidth = 2;
		gridBag.gridy = 10;
		panel.add(new Label("Délka normál (potvrdit Enterem)"), gridBag);

		gridBag.gridx = 2;
		TextField normalLength = new TextField(1);
		normalLength.setText("0.1");
		panel.add(normalLength, gridBag);

		normalLength.addActionListener(e -> {

			if (!normalLength.getText().isEmpty()) {

				try {
					renderer.setNormalLength(Float.parseFloat(normalLength.getText()));
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "C'mon! It has to be number...");
				}

			}
		});

		gridBag.ipady = 0;
		gridBag.gridx = 0;
		gridBag.gridwidth = 2;
		gridBag.gridy = 11;
		createAnimateCheckBox(panel, "animate normals", gridBag);

		gridBag.ipady = 0;
		gridBag.gridx = 0;
		gridBag.gridwidth = 2;
		gridBag.gridy = 12;
		panel.add(new Label("Mesh size (square)"), gridBag);

		gridBag.gridx = 2;
		TextField meshSize = new TextField(3);
		meshSize.setText("10");
		panel.add(meshSize, gridBag);

		meshSize.addActionListener(e -> {

			if (!meshSize.getText().isEmpty()) {

				try {
					int inputVal = Integer.parseInt(meshSize.getText());

					if (inputVal > 200) {
						JOptionPane.showMessageDialog(this, "Wou! 200 is max!");
					} else {
						renderer.setMeshSize(inputVal);
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this, "C'mon! It has to be number...");
				}

			}
		});

		frame.add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void createAnimateCheckBox(JPanel panel, String label, GridBagConstraints gridBag) {
		JCheckBox animateNormals = createCheckBox(panel, label, gridBag);

		animateNormals.addItemListener(e -> {
			if (animateNormals.isSelected()) {

				renderer.setAnimateNormals(1);

			} else {
				renderer.setAnimateNormals(0);

			}
		});

		animateNormals.setSelected(true);
	}

	private void createTransparencyCheckbox(JPanel panel, String label, GridBagConstraints gridBag) {
		JCheckBox transparency = createCheckBox(panel, label, gridBag);

		transparency.addItemListener(e -> {
			if (transparency.isSelected()) {

				renderer.setTransparency(1);

			} else {
				renderer.setTransparency(0);

			}
		});
	}

	private void createReflectorCheckBox(JPanel panel, String label, GridBagConstraints gridBag) {
		JCheckBox reflector = createCheckBox(panel, label, gridBag);

		reflector.addItemListener(e -> {
			if (reflector.isSelected()) {

				renderer.setIsReflector(1);

			} else {
				renderer.setIsReflector(0);

			}
		});
	}

	private void createAttenuationCheckBox(JPanel panel, String label, GridBagConstraints gridBag) {
		JCheckBox attenuation = createCheckBox(panel, label, gridBag);

		attenuation.addItemListener(e -> {
			if (attenuation.isSelected()) {

				renderer.setAttenuation(1);

			} else {
				renderer.setAttenuation(0);

			}
		});
	}

	private JCheckBox createCheckBox(JPanel panel, String label, GridBagConstraints gridBag) {
		JLabel checkboxlabel = new JLabel(label);

		panel.add(checkboxlabel, gridBag);

		JCheckBox checkbox = new JCheckBox();
		checkbox.setSelected(false);

		gridBag.gridx = 2;
		panel.add(checkbox, gridBag);
		return checkbox;
	}
}
