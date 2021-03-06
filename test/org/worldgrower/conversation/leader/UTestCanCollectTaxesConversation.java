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
package org.worldgrower.conversation.leader;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.worldgrower.Constants;
import org.worldgrower.DoNothingWorldOnTurn;
import org.worldgrower.TestUtils;
import org.worldgrower.World;
import org.worldgrower.WorldImpl;
import org.worldgrower.WorldObject;
import org.worldgrower.attribute.BuildingList;
import org.worldgrower.attribute.IdRelationshipMap;
import org.worldgrower.conversation.ConversationContext;
import org.worldgrower.conversation.Conversations;
import org.worldgrower.conversation.Question;
import org.worldgrower.conversation.Response;
import org.worldgrower.goal.GroupPropertyUtils;

public class UTestCanCollectTaxesConversation {

	private final CanCollectTaxesConversation conversation = Conversations.CAN_COLLECT_TAXES_CONVERSATION;
	
	@Test
	public void testGetReplyPhrases() {
		WorldObject performer = TestUtils.createIntelligentWorldObject(1, Constants.RELATIONSHIPS, new IdRelationshipMap());
		WorldObject target = TestUtils.createIntelligentWorldObject(2, Constants.RELATIONSHIPS, new IdRelationshipMap());
		
		ConversationContext context = new ConversationContext(performer, target, null, null, null, 0);
		List<Response> replyPhrases = conversation.getReplyPhrases(context);
		assertEquals(2, replyPhrases.size());
		assertEquals("Yes, you may collect taxes", replyPhrases.get(0).getResponsePhrase());
		assertEquals("No, you may not collect taxes", replyPhrases.get(1).getResponsePhrase());
	}
	
	@Test
	public void testGetReplyPhrase() {
		World world = new WorldImpl(1, 1, null, new DoNothingWorldOnTurn());
		WorldObject performer = TestUtils.createIntelligentWorldObject(7, Constants.BUILDINGS, new BuildingList());
		WorldObject target = TestUtils.createIntelligentWorldObject(8, Constants.BUILDINGS, new BuildingList());

		createVillagersOrganization(world);
				
		ConversationContext context = new ConversationContext(performer, target, null, null, world, 0);
		assertEquals(0, conversation.getReplyPhrase(context).getId());
	}
	
	@Test
	public void testGetQuestionPhrases() {
		WorldObject performer = TestUtils.createIntelligentWorldObject(1, Constants.NAME, "performer");
		WorldObject target = TestUtils.createIntelligentWorldObject(2, Constants.NAME, "target");
		
		List<Question> questions = conversation.getQuestionPhrases(performer, target, null, null, null);
		assertEquals(1, questions.size());
		assertEquals("I'd like permission to collect taxes. Is that ok?", questions.get(0).getQuestionPhrase());
	}
	
	@Test
	public void testIsConversationAvailable() {
		World world = new WorldImpl(1, 1, null, null);
		WorldObject performer = TestUtils.createIntelligentWorldObject(2, Constants.NAME, "performer");
		WorldObject target = TestUtils.createIntelligentWorldObject(3, Constants.NAME, "target");
		
		WorldObject organization = createVillagersOrganization(world);
		
		assertEquals(false,  conversation.isConversationAvailable(performer, target, null, world));
		
		organization.setProperty(Constants.ORGANIZATION_LEADER_ID, target.getProperty(Constants.ID));
		assertEquals(true,  conversation.isConversationAvailable(performer, target, null, world));
	}
	
	@Test
	public void testHandleResponse0() {
		WorldObject performer = TestUtils.createIntelligentWorldObject(1, Constants.RELATIONSHIPS, new IdRelationshipMap());
		WorldObject target = TestUtils.createIntelligentWorldObject(2, Constants.RELATIONSHIPS, new IdRelationshipMap());
		assertEquals(null, performer.getProperty(Constants.CAN_COLLECT_TAXES));
		
		ConversationContext context = new ConversationContext(performer, target, null, null, null, 0);
		
		conversation.handleResponse(0, context);
		assertEquals(Boolean.TRUE, performer.getProperty(Constants.CAN_COLLECT_TAXES));
	}

	private WorldObject createVillagersOrganization(World world) {
		WorldObject organization = GroupPropertyUtils.createVillagersOrganization(world);
		organization.setProperty(Constants.ID, 1);
		world.addWorldObject(organization);
		return organization;
	}
}
