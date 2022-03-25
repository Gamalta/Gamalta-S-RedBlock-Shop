package fr.gamalta.redblock.shop.utils.type;

public enum ButtonType {

	EXIT(false),
	PREVIOUS(false),
	NEXT(false),
	BUY_MORE(false),
	SELL_MORE(false),
	SELL_ALL(false),
	SET_MAX_STACK_SIZE(true),
	SET_1(true),
	ADD_10(true),
	ADD_1(true),
	REMOVE_10(true),
	REMOVE_1(true),
	BUY_VALIDATE(false),
	SELL_VALIDATE(false),
	NONE(false);

	boolean edit;

	ButtonType(boolean edit) {

		this.edit = edit;
	}

	public boolean canEdit() {
		return edit;
	}
}