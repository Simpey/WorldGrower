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
package org.worldgrower.attribute;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.worldgrower.generator.Item;

public class UTestItemCountMap {

	@Test
	public void testAdd() {
		ItemCountMap itemCountMap = new ItemCountMap();
		itemCountMap.add(Item.BERRIES, 5);
		
		assertEquals(5, itemCountMap.get(Item.BERRIES));
		
		itemCountMap.add(Item.BERRIES, 5);
		assertEquals(10, itemCountMap.get(Item.BERRIES));
	}
	
	@Test
	public void testAddWorldObject() {
		ItemCountMap itemCountMap = new ItemCountMap();
		itemCountMap.add(Item.BERRIES.generate(1f));
		
		assertEquals(1, itemCountMap.get(Item.BERRIES));
		
		itemCountMap.add(Item.BERRIES.generate(1f));
		assertEquals(2, itemCountMap.get(Item.BERRIES));
	}
	
	@Test
	public void testClear() {
		ItemCountMap itemCountMap = new ItemCountMap();
		itemCountMap.add(Item.BERRIES, 5);
		
		assertEquals(true, itemCountMap.contains(Item.BERRIES));
		itemCountMap.clear();
		assertEquals(false, itemCountMap.contains(Item.BERRIES));
	}
	
	@Test
	public void testGetItems() {
		ItemCountMap itemCountMap = new ItemCountMap();
		
		assertEquals(Arrays.asList(), itemCountMap.getItems());
		
		itemCountMap.add(Item.BERRIES, 5);
		assertEquals(Arrays.asList(Item.BERRIES), itemCountMap.getItems());
	}
	
	@Test
	public void testContainsAny() {
		ItemCountMap itemCountMap = new ItemCountMap();
		itemCountMap.add(Item.BERRIES, 5);
		
		assertEquals(true, itemCountMap.containsAny(Arrays.asList(Item.BERRIES, Item.BED)));
		assertEquals(false, itemCountMap.containsAny(Arrays.asList(Item.COTTON, Item.BED)));
	}
}

