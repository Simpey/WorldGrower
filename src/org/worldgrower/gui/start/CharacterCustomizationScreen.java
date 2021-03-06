/*******************************************************************************
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.worldgrower.gui.start;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.worldgrower.Constants;
import org.worldgrower.attribute.IntProperty;
import org.worldgrower.gui.GuiAttributeDescription;
import org.worldgrower.gui.ImageInfoReader;
import org.worldgrower.gui.SwingUtils;
import org.worldgrower.gui.TiledImagePanel;
import org.worldgrower.gui.cursor.Cursors;
import org.worldgrower.gui.music.MusicPlayer;
import org.worldgrower.gui.music.SoundIdReader;
import org.worldgrower.gui.util.DialogUtils;
import org.worldgrower.gui.util.IconUtils;
import org.worldgrower.gui.util.JButtonFactory;
import org.worldgrower.gui.util.JLabelFactory;

public class CharacterCustomizationScreen extends JFrame {

	private static final String ATTRIBUTE_EXPLANATION = "Shows number of attribute points that can be distributed among attributes. Attributes must be between 8 and 20";
	private JPanel contentPane;
	private int attributePoints = 6;
	
	private List<JButton> plusButtons = new ArrayList<>();
	private List<JButton> minButtons = new ArrayList<>();
	private JLabel lblStrength;
	private JLabel lblConstitution;
	private JLabel lblDexterity;
	private JLabel lblIntelligence;
	private JLabel lblWisdom;
	private JLabel lblCharisma;
	private JButton btnOk;
	
	public CharacterCustomizationScreen(ImageInfoReader imageInfoReader, SoundIdReader soundIdReader, MusicPlayer musicPlayer, KeyBindings keyBindings, JFrame parentFrame) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 539, 310);
		setResizable(false);
		contentPane = new TiledImagePanel(imageInfoReader);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(getSize());
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
		setUndecorated(true);
		IconUtils.setIcon(this);
		setCursor(Cursors.CURSOR);
		((JComponent)getRootPane()).setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
		
		JLabel attributeLabel = JLabelFactory.createJLabel(attributePoints);
		attributeLabel.setToolTipText(ATTRIBUTE_EXPLANATION);
		attributeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		attributeLabel.setBounds(413, 61, 56, 20);
		contentPane.add(attributeLabel);
		
		JLabel lblStrengthDesc = createAttributeLabel(Constants.STRENGTH, "Strength");
		lblStrengthDesc.setBounds(12, 23, 100, 20);
		contentPane.add(lblStrengthDesc);
		
		lblStrength = JLabelFactory.createJLabel("10");
		lblStrength.setBounds(115, 23, 24, 20);
		lblStrength.setToolTipText(lblStrengthDesc.getToolTipText());
		contentPane.add(lblStrength);
		
		JLabel lblConstitutionDesc = createAttributeLabel(Constants.CONSTITUTION, "Constitution");
		lblConstitutionDesc.setBounds(12, 61, 100, 20);
		contentPane.add(lblConstitutionDesc);
		
		lblConstitution = JLabelFactory.createJLabel("10");
		lblConstitution.setBounds(115, 61, 24, 20);
		lblConstitution.setToolTipText(lblConstitutionDesc.getToolTipText());
		contentPane.add(lblConstitution);
		
		JLabel lblDexterityDesc = createAttributeLabel(Constants.DEXTERITY, "Dexterity");
		lblDexterityDesc.setBounds(12, 101, 100, 20);
		contentPane.add(lblDexterityDesc);
		
		lblDexterity = JLabelFactory.createJLabel("10");
		lblDexterity.setBounds(115, 101, 24, 20);
		lblDexterity.setToolTipText(lblDexterityDesc.getToolTipText());
		contentPane.add(lblDexterity);
		
		JLabel lblIntelligenceDesc = createAttributeLabel(Constants.INTELLIGENCE, "Intelligence");
		lblIntelligenceDesc.setBounds(12, 143, 100, 20);
		contentPane.add(lblIntelligenceDesc);
		
		lblIntelligence = JLabelFactory.createJLabel("10");
		lblIntelligence.setBounds(115, 143, 24, 20);
		lblIntelligence.setToolTipText(lblIntelligenceDesc.getToolTipText());
		contentPane.add(lblIntelligence);
		
		JLabel lblWisdomDesc = createAttributeLabel(Constants.WISDOM, "Wisdom");
		lblWisdomDesc.setBounds(12, 189, 100, 20);
		contentPane.add(lblWisdomDesc);
		
		lblWisdom = JLabelFactory.createJLabel("10");
		lblWisdom.setBounds(115, 189, 24, 20);
		lblWisdom.setToolTipText(lblWisdomDesc.getToolTipText());
		contentPane.add(lblWisdom);
		
		JLabel lblCharismaDesc = createAttributeLabel(Constants.CHARISMA, "Charisma");
		lblCharismaDesc.setBounds(12, 234, 100, 20);
		contentPane.add(lblCharismaDesc);
		
		lblCharisma = JLabelFactory.createJLabel("10");
		lblCharisma.setBounds(115, 234, 24, 20);
		lblCharisma.setToolTipText(lblCharismaDesc.getToolTipText());
		contentPane.add(lblCharisma);
		
		JButton button1Plus = JButtonFactory.createButton("+", imageInfoReader, soundIdReader);
		plusButtons.add(button1Plus);
		button1Plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				incrementAttributeValue(lblStrength, attributeLabel); 
			}
		});
		button1Plus.setBounds(214, 21, 56, 25);
		contentPane.add(button1Plus);
		
		JButton button1Min = JButtonFactory.createButton("-", imageInfoReader, soundIdReader);
		minButtons.add(button1Min);
		button1Min.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				decrementAttributeValue(lblStrength, attributeLabel); 
			}
		});
		button1Min.setBounds(146, 21, 56, 25);
		contentPane.add(button1Min);
		
		JButton button = JButtonFactory.createButton("+", imageInfoReader, soundIdReader);
		plusButtons.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				incrementAttributeValue(lblConstitution, attributeLabel); 
			}
		});
		button.setBounds(214, 59, 56, 25);
		contentPane.add(button);
		
		JButton button_1 = JButtonFactory.createButton("-", imageInfoReader, soundIdReader);
		minButtons.add(button_1);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				decrementAttributeValue(lblConstitution, attributeLabel); 
			}
		});
		button_1.setBounds(146, 59, 56, 25);
		contentPane.add(button_1);
		
		JButton button_2 = JButtonFactory.createButton("+", imageInfoReader, soundIdReader);
		plusButtons.add(button_2);
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				incrementAttributeValue(lblDexterity, attributeLabel); 
			}
		});
		button_2.setBounds(214, 99, 56, 25);
		contentPane.add(button_2);
		
		JButton button_3 = JButtonFactory.createButton("-", imageInfoReader, soundIdReader);
		minButtons.add(button_3);
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				decrementAttributeValue(lblDexterity, attributeLabel); 
			}
		});
		button_3.setBounds(146, 99, 56, 25);
		contentPane.add(button_3);
		
		JButton button_4 = JButtonFactory.createButton("+", imageInfoReader, soundIdReader);
		plusButtons.add(button_4);
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				incrementAttributeValue(lblIntelligence, attributeLabel); 
			}
		});
		button_4.setBounds(214, 141, 56, 25);
		contentPane.add(button_4);
		
		JButton button_5 = JButtonFactory.createButton("-", imageInfoReader, soundIdReader);
		minButtons.add(button_5);
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				decrementAttributeValue(lblIntelligence, attributeLabel); 
			}
		});
		button_5.setBounds(146, 141, 56, 25);
		contentPane.add(button_5);
		
		JButton button_6 = JButtonFactory.createButton("+", imageInfoReader, soundIdReader);
		plusButtons.add(button_6);
		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				incrementAttributeValue(lblWisdom, attributeLabel); 
			}
		});
		button_6.setBounds(214, 187, 56, 25);
		contentPane.add(button_6);
		
		JButton button_7 = JButtonFactory.createButton("-", imageInfoReader, soundIdReader);
		minButtons.add(button_7);
		button_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				decrementAttributeValue(lblWisdom, attributeLabel); 
			}
		});
		button_7.setBounds(146, 187, 56, 25);
		contentPane.add(button_7);
		
		JButton button_8 = JButtonFactory.createButton("+", imageInfoReader, soundIdReader);
		plusButtons.add(button_8);
		button_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				incrementAttributeValue(lblCharisma, attributeLabel); 
			}
		});
		button_8.setBounds(214, 232, 56, 25);
		contentPane.add(button_8);
		
		JButton button_9 = JButtonFactory.createButton("-", imageInfoReader, soundIdReader);
		minButtons.add(button_9);
		button_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				decrementAttributeValue(lblCharisma, attributeLabel); 
			}
		});
		button_9.setBounds(146, 232, 56, 25);
		contentPane.add(button_9);
		
		setButtonTooltipTexts();
		
		JLabel lblRemainingAttributePoints = JLabelFactory.createJLabel("Remaining attribute points:");
		lblRemainingAttributePoints.setToolTipText(ATTRIBUTE_EXPLANATION);
		lblRemainingAttributePoints.setBounds(314, 23, 215, 25);
		contentPane.add(lblRemainingAttributePoints);
		
		btnOk = JButtonFactory.createButton("Ok", imageInfoReader, soundIdReader);
		btnOk.setBounds(422, 264, 97, 25);
		getRootPane().setDefaultButton(btnOk);
		contentPane.add(btnOk);
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CharacterCustomizationScreen.this.setVisible(false);
				
				CharacterAttributes characterAttributes = new CharacterAttributes(
						Integer.parseInt(lblStrength.getText()),
						Integer.parseInt(lblConstitution.getText()),
						Integer.parseInt(lblDexterity.getText()),
						Integer.parseInt(lblIntelligence.getText()),
						Integer.parseInt(lblWisdom.getText()),
						Integer.parseInt(lblCharisma.getText())
						);
				
				OptionsScreen window = new OptionsScreen(characterAttributes, imageInfoReader, soundIdReader, musicPlayer, keyBindings, parentFrame);
				window.setVisible(true);
			}
		});
		
		if (parentFrame != null) {
			SwingUtils.installEscapeCloseOperation(this);
			DialogUtils.createDialogBackPanel(this, parentFrame.getContentPane());
		}
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		btnOk.requestFocusInWindow();
	}

	private void setButtonTooltipTexts() {
		for(JButton minButton : minButtons) {
			minButton.setToolTipText("Decrease attribute value");
		}
		
		for(JButton plusButton : plusButtons) {
			plusButton.setToolTipText("Increase attribute value");
		}
	}
	
	private JLabel createAttributeLabel(IntProperty attributeProperty, String description) {
		String tooltip = GuiAttributeDescription.createToolTipDescription(attributeProperty, description);
		JLabel label = JLabelFactory.createJLabel(description);
		label.setToolTipText(tooltip);
		return label;
	}

	private void incrementAttributeValue(JLabel label, JLabel attributeLabel) {
		int currentValue = Integer.parseInt(label.getText());
		int newValue = currentValue + 1;
		label.setText(Integer.toString(newValue));
		
		attributePoints--;
		attributeLabel.setText(Integer.toString(attributePoints));
		
		handleMinMaxValues();
		
		if (attributePoints == 0) {
			for(JButton plusButton : plusButtons) {
				plusButton.setEnabled(false);
			}
		}
	}
	
	private void handleMinMaxValues() {
		int strength = Integer.parseInt(lblStrength.getText());
		handleMinMaxValue(strength, 0);
		
		int constitution = Integer.parseInt(lblConstitution.getText());
		handleMinMaxValue(constitution, 1);
		
		int dexterity = Integer.parseInt(lblDexterity.getText());
		handleMinMaxValue(dexterity, 2);
		
		int intelligence = Integer.parseInt(lblIntelligence.getText());
		handleMinMaxValue(intelligence, 3);
		
		int wisdom = Integer.parseInt(lblWisdom.getText());
		handleMinMaxValue(wisdom, 4);
		
		int charisma = Integer.parseInt(lblCharisma.getText());
		handleMinMaxValue(charisma, 5);
	}
	
	private void handleMinMaxValue(int attributeValue, int buttonIndex) {
		if (attributeValue == 20) {
			plusButtons.get(buttonIndex).setEnabled(false);
		} else if (attributeValue == 8) {
			minButtons.get(buttonIndex).setEnabled(false);
		} else {
			plusButtons.get(buttonIndex).setEnabled(true);
			minButtons.get(buttonIndex).setEnabled(true);
		}
	}

	private void decrementAttributeValue(JLabel label, JLabel attributeLabel) {
		int currentValue = Integer.parseInt(label.getText());
		int newValue = currentValue - 1;
		label.setText(Integer.toString(newValue));
		
		attributePoints++;
		attributeLabel.setText(Integer.toString(attributePoints));
		
		handleMinMaxValues();
		
		if (attributePoints > 0) {
			for(JButton plusButton : plusButtons) {
				plusButton.setEnabled(true);
			}
		}
	}
}
