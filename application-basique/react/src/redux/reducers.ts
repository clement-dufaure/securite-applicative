const initial = {
};

export default (state = initial, action) => {
	switch (action.type) {
		case "app/init": {
			return { ...state, keycloak: action.keycloak };
		}
		default:
			return state;
	}
};
