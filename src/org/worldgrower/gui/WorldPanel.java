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
package org.worldgrower.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.worldgrower.Constants;
import org.worldgrower.DungeonMaster;
import org.worldgrower.ManagedOperation;
import org.worldgrower.ManagedOperationListener;
import org.worldgrower.World;
import org.worldgrower.WorldObject;
import org.worldgrower.actions.BuildAction;
import org.worldgrower.attribute.LookDirection;
import org.worldgrower.condition.Condition;
import org.worldgrower.gui.conversation.GuiRespondToQuestion;
import org.worldgrower.terrain.Terrain;
import org.worldgrower.terrain.TerrainType;

public class WorldPanel extends JPanel {

	private WorldObject playerCharacter;
	private World world;
	private ImageInfoReader imageInfoReader = new ImageInfoReader();
	private GuiMouseListener guiMouseListener;
	private int offsetX = 0;
	private int offsetY = 0;
	
	private JTextArea messageTextArea;
	private JProgressBar hitPointsProgressBar;
	private JProgressBar foodTextProgressBar;
	private JProgressBar waterProgressBar;
	private JProgressBar energyProgressBar;
	
	private BuildModeOutline buildModeOutline = new BuildModeOutline();
	private MouseMotionListener mouseMotionListener;
	
    public WorldPanel(WorldObject playerCharacter, World world, DungeonMaster dungeonMaster) throws IOException {
        super(new BorderLayout());

        guiMouseListener = new GuiMouseListener(this, playerCharacter, world, dungeonMaster, imageInfoReader);
		addMouseListener(guiMouseListener);

        int width = 1024;
        int height = 768;
        
        setBounds(0, 0, width, height);
        this.setMinimumSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");
        getActionMap().put("Cancel", new ShowStartScreenAction(world));
        
        getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, 0), "up");
        getActionMap().put("up", new GuiMoveAction(new int[] { 0,  -1 }, playerCharacter, world, dungeonMaster, this));
        
        getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, 0), "down");
        getActionMap().put("down", new GuiMoveAction(new int[] { 0,  1 }, playerCharacter, world, dungeonMaster, this));
        
        getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, 0), "left");
        getActionMap().put("left", new GuiMoveAction(new int[] { -1,  0 }, playerCharacter, world, dungeonMaster, this));
        
        getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, 0), "right");
        getActionMap().put("right", new GuiMoveAction(new int[] { 1,  0 }, playerCharacter, world, dungeonMaster, this));

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, 0), "7");
        getActionMap().put("7", new GuiMoveAction(new int[] { -1,  -1 }, playerCharacter, world, dungeonMaster, this));
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, 0), "9");
        getActionMap().put("9", new GuiMoveAction(new int[] { 1,  -1 }, playerCharacter, world, dungeonMaster, this));

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, 0), "1");
        getActionMap().put("1", new GuiMoveAction(new int[] { -1,  1 }, playerCharacter, world, dungeonMaster, this));
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, 0), "3");
        getActionMap().put("3", new GuiMoveAction(new int[] { 1,  1 }, playerCharacter, world, dungeonMaster, this));
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.RED);
        SpringLayout layout = new SpringLayout();
		infoPanel.setLayout(layout);
		makeUnfocussable(infoPanel);
        
        messageTextArea = new JTextArea(3, 30);
        messageTextArea.setEditable(false);
        messageTextArea.setToolTipText("This area displays messages like combat or dialogues");
        makeUnfocussable(messageTextArea);
        world.addListener(new MessageManagedOperationListener());
        
        hitPointsProgressBar = new JProgressBar(JProgressBar.VERTICAL, 0, playerCharacter.getProperty(Constants.HIT_POINTS_MAX));
        hitPointsProgressBar.setBackground(Color.BLACK);
        hitPointsProgressBar.setForeground(Color.RED);
        hitPointsProgressBar.setToolTipText("hit points");
        makeUnfocussable(hitPointsProgressBar);
        
        infoPanel.add(hitPointsProgressBar);
        layout.putConstraint(SpringLayout.WEST, hitPointsProgressBar, 0, SpringLayout.EAST, messageTextArea);
        layout.putConstraint(SpringLayout.NORTH, hitPointsProgressBar, 0, SpringLayout.NORTH, messageTextArea);
        layout.putConstraint(SpringLayout.SOUTH, hitPointsProgressBar, 0, SpringLayout.SOUTH, messageTextArea);
        
        foodTextProgressBar = new JProgressBar(JProgressBar.VERTICAL, 0, 1000);
        foodTextProgressBar.setBackground(Color.BLACK);
        foodTextProgressBar.setForeground(Color.YELLOW);
        foodTextProgressBar.setToolTipText("food");
        makeUnfocussable(foodTextProgressBar);
        
        infoPanel.add(foodTextProgressBar);
        layout.putConstraint(SpringLayout.WEST, foodTextProgressBar, 0, SpringLayout.EAST, hitPointsProgressBar);
        layout.putConstraint(SpringLayout.NORTH, foodTextProgressBar, 0, SpringLayout.NORTH, hitPointsProgressBar);
        layout.putConstraint(SpringLayout.SOUTH, foodTextProgressBar, 0, SpringLayout.SOUTH, messageTextArea);
        
        waterProgressBar = new JProgressBar(JProgressBar.VERTICAL, 0, 1000);
        waterProgressBar.setBackground(Color.BLACK);
        waterProgressBar.setForeground(Color.BLUE);
        waterProgressBar.setToolTipText("water");
        makeUnfocussable(waterProgressBar);
        
        infoPanel.add(waterProgressBar);
        layout.putConstraint(SpringLayout.WEST, waterProgressBar, 0, SpringLayout.EAST, foodTextProgressBar);
        layout.putConstraint(SpringLayout.NORTH, waterProgressBar, 0, SpringLayout.NORTH, foodTextProgressBar);
        layout.putConstraint(SpringLayout.SOUTH, waterProgressBar, 0, SpringLayout.SOUTH, messageTextArea);

        energyProgressBar = new JProgressBar(JProgressBar.VERTICAL, 0, 1000);
        energyProgressBar.setBackground(Color.BLACK);
        energyProgressBar.setForeground(Color.GREEN);
        energyProgressBar.setToolTipText("energy");
        makeUnfocussable(energyProgressBar);
        
        infoPanel.add(energyProgressBar);
        layout.putConstraint(SpringLayout.WEST, energyProgressBar, 0, SpringLayout.EAST, waterProgressBar);
        layout.putConstraint(SpringLayout.NORTH, energyProgressBar, 0, SpringLayout.NORTH, waterProgressBar);
        layout.putConstraint(SpringLayout.SOUTH, energyProgressBar, 0, SpringLayout.SOUTH, messageTextArea);

        infoPanel.add(messageTextArea);
        layout.putConstraint(SpringLayout.WEST, messageTextArea, 0, SpringLayout.WEST, infoPanel);
        layout.putConstraint(SpringLayout.NORTH, messageTextArea, 0, SpringLayout.NORTH, infoPanel);
        
        add(infoPanel, BorderLayout.SOUTH);
        
        layout.putConstraint(SpringLayout.EAST, infoPanel, 0, SpringLayout.EAST, energyProgressBar);
        layout.putConstraint(SpringLayout.SOUTH, infoPanel, 0, SpringLayout.SOUTH, messageTextArea);
        
        this.playerCharacter = playerCharacter;
        this.world = world;
    }
    
    private class MessageManagedOperationListener implements ManagedOperationListener {

		@Override
		public void actionPerformed(ManagedOperation managedOperation, WorldObject performer, WorldObject target, int[] args, Object message) {
			if (performer.equals(playerCharacter) || target.equals(playerCharacter)) {
				messageTextArea.setText(message.toString());
			}
		}
    }
    
    private void makeUnfocussable(JComponent component) {
    	component.setRequestFocusEnabled(false);
    	component.setFocusable(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for(int x = 0; x<world.getWidth() ;x++) {
			for(int y = 0; y<world.getHeight(); y++) {
				g.setColor(getBackgroundColor(x, y));
				g.fillRect((x+offsetX) * 48, (y+offsetY) * 48, 48, 48);
			}
		}
        
		List<WorldObject> worldObjects = world.getWorldObjects();
		for(WorldObject worldObject : worldObjects) {
			ImageIds id = getImageId(worldObject);
			LookDirection lookDirection = getLookDirection(worldObject);
    		Image image = imageInfoReader.getImage(id, lookDirection);
			
			int x = worldObject.getProperty(Constants.X);
			int y = worldObject.getProperty(Constants.Y);
			
			if (world.getTerrain().isExplored(x, y) && isWorldObjectVisible(worldObject)) {
				image = changeSize(worldObject, image);
				g.drawImage(image, (x+offsetX) * 48, (y+offsetY) * 48, null);
				
				ImageIds overlayingImageId = getOverlayingImageId(worldObject);
				if (overlayingImageId != null) {
					Image overlayingImage = imageInfoReader.getImage(overlayingImageId, lookDirection);
					g.drawImage(overlayingImage, (x+offsetX) * 48, (y+offsetY) * 48, null);
				}
			}
		}
		
		hitPointsProgressBar.setValue(playerCharacter.getProperty(Constants.HIT_POINTS));
		foodTextProgressBar.setValue(playerCharacter.getProperty(Constants.FOOD));
		waterProgressBar.setValue(playerCharacter.getProperty(Constants.WATER));
		energyProgressBar.setValue(playerCharacter.getProperty(Constants.ENERGY));
		buildModeOutline.repaintBuildMode(g, getMouseLocation(), offsetX, offsetY, playerCharacter, world);
    }

	private Image changeSize(WorldObject worldObject, Image image) {
		if (hasCondition(worldObject, Condition.ENLARGED_CONDITION)) {
			int imageWidth = 48 * worldObject.getProperty(Constants.WIDTH);
			int imageHeight = 48 * worldObject.getProperty(Constants.HEIGHT);
			image = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
		}
		if (hasCondition(worldObject, Condition.REDUCED_CONDITION)) {
			final int imageWidth;
			if (worldObject.getProperty(Constants.ORIGINAL_WIDTH) == 1) {
				imageWidth = 24 * worldObject.getProperty(Constants.WIDTH);
			} else {
				imageWidth = 48 * worldObject.getProperty(Constants.WIDTH);
			}
			final int imageHeight;
			if (worldObject.getProperty(Constants.ORIGINAL_HEIGHT) == 1) {
				imageHeight = 24 * worldObject.getProperty(Constants.HEIGHT);
			} else {
				imageHeight = 48 * worldObject.getProperty(Constants.HEIGHT);
			}
			image = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
		}
		return image;
	}

    private boolean isWorldObjectVisible(WorldObject worldObject) {
		if (worldObject.equals(playerCharacter)) {
			return true;
		} else if (worldObject.hasProperty(Constants.CONDITIONS) && worldObject.getProperty(Constants.CONDITIONS).hasCondition(Condition.INVISIBLE_CONDITION)) {
			return false;
		} else {
			return true;
		}
	}

	private ImageIds getImageId(WorldObject worldObject) {
		WorldObject facade = worldObject.getProperty(Constants.FACADE);
		if (worldObject.hasProperty(Constants.CONDITIONS) && worldObject.getProperty(Constants.CONDITIONS).hasCondition(Condition.COCOONED_CONDITION)) {
			return ImageIds.COCOON;
		} else if ((facade != null) && facade.getProperty(Constants.IMAGE_ID) != null) {
			return facade.getProperty(Constants.IMAGE_ID);
		} else {
			return worldObject.getProperty(Constants.IMAGE_ID);
		}
	}
    
    private ImageIds getOverlayingImageId(WorldObject worldObject) {
    	if (hasCondition(worldObject, Condition.BURNING_CONDITION)) {
    		return ImageIds.BURNING;
    	} else if (hasCondition(worldObject, Condition.INVISIBLE_CONDITION)) {
    		return ImageIds.INVISIBILITY_INDICATOR;
    	} else if (hasCondition(worldObject, Condition.POISONED_CONDITION)) {
    		return ImageIds.POISONED_INDICATOR;
    	} else if (hasCondition(worldObject, Condition.SLEEP_CONDITION)) {
    		return ImageIds.SLEEPING_INDICATOR;
    	} else if (hasCondition(worldObject, Condition.PARALYZED_CONDITION)) {
    		return ImageIds.PARALYZED_INDICATOR;
    	} else {
    		return null;
    	}
    }

	private boolean hasCondition(WorldObject worldObject, Condition condition) {
		return worldObject.hasProperty(Constants.CONDITIONS) && worldObject.getProperty(Constants.CONDITIONS).hasCondition(condition);
	}
    
	private LookDirection getLookDirection(WorldObject worldObject) {
		if (worldObject.hasProperty(Constants.LOOK_DIRECTION)) {
			return worldObject.getProperty(Constants.LOOK_DIRECTION);
		} else {
			return null;
		}
	}
    
    public void centerOffsetsOn(int x, int y) {
    	int width = this.getWidth() / 48;
    	int height = this.getHeight() / 48;
    	this.offsetX = offsetX - (x - width / 2);
    	this.offsetY = offsetY - (y - height / 2);
    	
    	if (offsetX > 0) {
    		offsetX = 0;
    	}
    	if (offsetY > 0) {
    		offsetY = 0;
    	}
    	if (offsetX < -world.getWidth() + width) {
    		offsetX = -world.getWidth() + width;
    	}
    	if (offsetY < -world.getHeight() + height) {
    		offsetY = -world.getHeight() + height;
    	}
    }
    
	private Color getBackgroundColor(int x, int y) {
		final Color backgroundColor;
		Terrain terrain = world.getTerrain();
		if (terrain.isExplored(x, y)) {
			TerrainType terrainType = terrain.getTerrainInfo(x, y).getTerrainType();
			switch(terrainType) {
				case GRASLAND:
					backgroundColor = new Color(110, 196, 88);
					break;
				case PLAINS:
					backgroundColor = new Color(235, 195, 75);
					break;
				case HILL:
					backgroundColor = new Color(171, 140, 17);
					break;
				case MOUNTAIN:
					backgroundColor = new Color(161, 161, 161);
					break;
				default:
					backgroundColor = Color.BLACK;
			}
		} else {
			backgroundColor = Color.BLACK;
		}
		return backgroundColor;
	}

	public WorldObject findWorldObject(int x, int y) {
        List<WorldObject> worldObjects = world.findWorldObjects(w -> w.getProperty(Constants.X) == (x-offsetX) && w.getProperty(Constants.Y) == (y-offsetY));
		final WorldObject worldObject;
		if (worldObjects.size() > 0) {
			worldObject = worldObjects.get(0);
		} else {
			worldObject = null;
		}
		return worldObject;
	}

	public void createGuiRespondToImage() {
		new GuiRespondToQuestion(playerCharacter, world, imageInfoReader);
		new GuiShowReadAction(playerCharacter, world, (JComponent) this.getParent(), imageInfoReader);
	}

	public void startBuildMode(BuildAction buildAction, int[] args) {
		this.buildModeOutline.startBuildMode(buildAction, args);
		this.mouseMotionListener = new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent mouseEvent) {
				super.mouseMoved(mouseEvent);
				WorldPanel.this.repaint();
			}
			
		};
		this.addMouseMotionListener(this.mouseMotionListener);
	}
	
	public void endBuildMode(boolean executeBuildAction) {
		this.buildModeOutline.endBuildMode(executeBuildAction, getMouseLocation(), offsetX, offsetY, playerCharacter, world, guiMouseListener);
		this.removeMouseMotionListener(this.mouseMotionListener);
	}

	private Point getMouseLocation() {
		Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mouseLocation, this);
		return mouseLocation;
	}
	
	public boolean inBuildMode() {
		return buildModeOutline.inBuildMode();
	}

	public void centerViewOnPlayerCharacter() {
		int x = playerCharacter.getProperty(Constants.X);
		int y = playerCharacter.getProperty(Constants.Y);
		
		int xInView = (x+offsetX) * 48;
		int yInView = (y+offsetY) * 48;
		
		if ((xInView < 48) || (xInView > this.getWidth() - 48) || (yInView < 48) || (yInView > this.getHeight() - 96)) {
			centerOffsetsOn(x+offsetX, y+offsetY);
		}
	}
}