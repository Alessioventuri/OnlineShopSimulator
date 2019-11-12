package com.online.shop.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import javax.swing.DefaultListModel;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.online.shop.controller.ShopController;
import com.online.shop.model.Item;

import org.junit.Test;

@RunWith(GUITestRunner.class)
public class ItemsViewSwingTest extends AssertJSwingJUnitTestCase{

	
	private FrameFixture window;
	private	ItemsViewSwing itemsViewSwing;

	@Mock
	private ShopController shopController;

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			itemsViewSwing = new ItemsViewSwing();
			itemsViewSwing.setShopController(shopController);
			return itemsViewSwing;
		});
		window = new FrameFixture(robot(), itemsViewSwing);
		window.show();

	}
	@Test @GUITest
	public void testControlsInitialStates() {
		window.textBox("itemName").requireEnabled();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.button(JButtonMatcher.withText("Remove")).requireDisabled();
		window.list("itemListShop");
		window.list("itemListCart");
		window.button(JButtonMatcher.withText("Search")).requireEnabled();
		window.label("errorMessageLabel").requireText(" ");
		window.button(JButtonMatcher.withText("Buy")).requireDisabled();
	}


	@Test
	public void testAddButtonShouldBeEnabledWhenAnItemIsSelectedInItemListShop() {
		GuiActionRunner.execute(
				()-> itemsViewSwing.getItemListShopModel().addElement(new Item("1","Iphone"))
		);
		window.list("itemListShop").selectItem(0);
		JButtonFixture addButton = window.button(JButtonMatcher.withText("Add"));
		addButton.requireEnabled();
		window.list("itemListShop").clearSelection();
		addButton.requireDisabled();
	}
	
	@Test
	public void testSearchButtonShouldShowFilteredShopListIfThereIsTheSearchedItem() {
		//setup
		Item item1 = new Item("1","Iphone");
		Item item2 = new Item("3","Samsung",1);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListShopModel = itemsViewSwing.getItemListShopModel();
					itemListShopModel.addElement(item1);
					itemListShopModel.addElement(item2);
				}
		);
		//execute
		window.textBox("itemName").setText("Iphone");
		GuiActionRunner.execute(() ->
			itemsViewSwing.showSearchResult(item1)
		);
		//verify
		String[] listContents = window.list("itemListShop").contents();
		assertThat(listContents).containsExactly(item1.toString());
	}
	@Test
	public void testSearchButtonShouldShowShopListIfTheSearchBoxIsEmpty() {
		//setup
		Item item1 = new Item("1","Iphone");
		Item item2 = new Item("3","Samsung",1);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListShopModel = itemsViewSwing.getItemListShopModel();
					itemListShopModel.addElement(item1);
					itemListShopModel.addElement(item2);
				}
		);
		//execute
		window.textBox("itemName").setText("");
		GuiActionRunner.execute(() ->
			itemsViewSwing.showSearchResult(item1)
		);
		//verify
		String[] listContents = window.list("itemListShop").contents();
		assertThat(listContents).containsExactly(item1.toString(),item2.toString());
	}
	@Test
	public void testBuyButtonShouldBeEnabledWhenThereIsOneOrMoreItemsInItemListCart() {
		GuiActionRunner.execute(
				()-> itemsViewSwing.getItemListCartModel().addElement(new Item("1","Iphone"))
		);
		window.list("itemListCart").selectItem(0);
		JButtonFixture buyButton = window.button(JButtonMatcher.withText("Remove"));	
		buyButton.requireEnabled();
		window.list("itemListCart").clearSelection();
		buyButton.requireDisabled();

	}
	@Test
	public void testDeleteButtonShouldBeEnabledWhenAnItemIsSelectedInItemListCart() {
		//setup
		GuiActionRunner.execute(
				()->{ 
					itemsViewSwing.getItemListCartModel().addElement(new Item("1","Iphone",1));
					itemsViewSwing.getItemListShopModel().addElement(new Item("1","Iphone", 19));
				}
		);
		//execute
		window.list("itemListCart").selectItem(0);
		//verify
		JButtonFixture deleteButton = window.button(JButtonMatcher.withText("Remove"));	
		deleteButton.requireEnabled();
		window.list("itemListCart").clearSelection();
		deleteButton.requireDisabled();
	}
	@Test
	public void testShowItemsShopShouldAddItemsToTheItemShopList() {
		//setup
		Item item1 = new Item("1","Iphone");
		Item item2 = new Item("3","Samsung");
		//execute
		GuiActionRunner.execute(() ->
			itemsViewSwing.showItemsShop(Arrays.asList(item1,item2))
		);
		//verify
		String[] listContents = window.list("itemListShop").contents();
		assertThat(listContents).containsExactly(item1.toString(),item2.toString());
	}
	@Test
	public void testErrorLogShouldShowTheMessageInTheErrorMessageLabel() {
		Item item = new Item("1","Iphone");
		GuiActionRunner.execute(
				() -> itemsViewSwing.errorLog("error Message", item)
		);
		window.label("errorMessageLabel").requireText("error Message: " + item);
	}
	@Test
	public void testItemAddedToCartShouldAddTheItemToTheItemListCartAndResetTheErrorLabel() {
		//setup
		Item item = new Item("1","Iphone");
		//execute
		GuiActionRunner.execute(
				()-> itemsViewSwing.itemAddedToCart(item)
		);
		//verify
		String[] listContents = window.list("itemListCart").contents();
		assertThat(listContents).containsExactly(item.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	@Test
	public void testItemAddedToCartShouldIncreseQuantityIfThereIsThatItemInTheCart() {
		//setup
		Item item = new Item("1","Iphone");

		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListCartModel = itemsViewSwing.getItemListCartModel();
					itemListCartModel.addElement(item);
		});

		//execute
		GuiActionRunner.execute(
				()-> itemsViewSwing.itemAddedToCart(item)
		);
		//verify
		String[] listContents = window.list("itemListCart").contents();
		assertThat(listContents).containsExactly(item.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	@Test
	public void testItemRemovedToCartShouldRemoveTheItemFromTheItemListCartIfQuantityIsZeroAndResetErrorLabel() {
		//setup
		Item item1 = new Item("1","Iphone",1);
		Item item2 = new Item("3","Samsung",1);

		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListCartModel = itemsViewSwing.getItemListCartModel();
					itemListCartModel.addElement(item1);
					itemListCartModel.addElement(item2);
				}
		);
		//execute
		GuiActionRunner.execute(
				()-> itemsViewSwing.itemRemovedToCart(item1)
		);
		//verify
		String[] listContents = window.list("itemListCart").contents();
		assertThat(listContents).containsExactly(item2.toString());
	}
	@Test
	public void testItemRemovedToCartShouldDecreseQuantityIfQuantityIsntZeroAndResetErrorLabel() {
		//setup
		Item item = new Item("1","Iphone",2);

		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListCartModel = itemsViewSwing.getItemListCartModel();
					itemListCartModel.addElement(item);
				}
		);
		//execute
		GuiActionRunner.execute(
				()-> itemsViewSwing.itemRemovedToCart(item)
		);
		//verify
		String[] listContents = window.list("itemListCart").contents();
		assertThat(listContents).containsExactly(item.toString());
	}

	@Test
	public void testAddButtonShouldDelegateToTheShopControllerAddElement() {
		//setup
		Item item = new Item("1","Iphone");
		GuiActionRunner.execute(
				()-> itemsViewSwing.getItemListShopModel().addElement(item)
		);
		//execute
		window.list("itemListShop").selectItem(0);
		window.button(JButtonMatcher.withText("Add")).click();
		//verify
		verify(shopController).addItemToCart(item);
		
	}

	@Test
	public void testRemoveButtonShouldDelegateToTheShopControllerRemoveElement() {
		//setup
		Item item = new Item("1","Iphone");
		GuiActionRunner.execute(
				()-> itemsViewSwing.getItemListCartModel().addElement(item)
		);
		//execute
		window.list("itemListCart").selectItem(0);
		window.button(JButtonMatcher.withText("Remove")).click();
		//verify
		verify(shopController).removeItemFromCart(item);
	}
	
	@Test
	public void testSearchButtonShouldDelegateToTheShopControllerShowSearchResults() {
		//setup
		Item item1 = new Item("1","Iphone");
		Item item2 = new Item("2", "Nokia");
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListShopModel = itemsViewSwing.getItemListShopModel();
					itemListShopModel.addElement(item1);
					itemListShopModel.addElement(item2);
		});
		window.textBox("itemName").enterText("Nokia");
		//execute
		window.button(JButtonMatcher.withText("Search")).click();
		//verify
		verify(shopController).searchItem(window.textBox("itemName").text());	

	}
	
	@Test
	public void testSearchButtonShouldReturnShopListIfTextFieldIsEmpty() {
		Item item1 = new Item("1","Iphone");
		Item item2 = new Item("2", "Nokia");
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListShopModel = itemsViewSwing.getItemListShopModel();
					itemListShopModel.addElement(item1);
					itemListShopModel.addElement(item2);
				}
		);
		window.textBox("itemName").enterText("");
		//execute
		window.button(JButtonMatcher.withText("Search")).click();
		//verify
		verify(shopController).searchItem(window.textBox("itemName").text());	

	}
	
	@Test
	public void testAddButtonShouldDelegateToTheShopControllerModifyQuantity() {
		//setup
		Item item = new Item("1","Iphone",5);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListShopModel = itemsViewSwing.getItemListShopModel();
					itemListShopModel.addElement(item);
					DefaultListModel<Item> itemListCartModel = itemsViewSwing.getItemListCartModel();
					itemListCartModel.addElement(item);
				}
		);
		window.list("itemListShop").selectItem(0);
		//execute
		window.button(JButtonMatcher.withText("Add")).click();
		//verify
		verify(shopController).addItemToCart(item);
	}
	@Test
	public void testDeleteButtonShouldDelegateToTheShopControllerModifyQuantity() {
		//setup
		Item item = new Item("1","Iphone",5);
		GuiActionRunner.execute(
				()-> {
					DefaultListModel<Item> itemListShopModel = itemsViewSwing.getItemListShopModel();
					itemListShopModel.addElement(item);
					DefaultListModel<Item> itemListCartModel = itemsViewSwing.getItemListCartModel();
					itemListCartModel.addElement(item);
				}
		);
		window.list("itemListCart").selectItem(0);
		//execute
		window.button(JButtonMatcher.withText("Remove")).click();
		//verify
		verify(shopController).removeItemFromCart(item);
	}
	

}
