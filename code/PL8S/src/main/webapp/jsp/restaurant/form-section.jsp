<hr class="full">
<section class="form_section">
	<form class="condensed-form">
		<div class="input-label-pair bigger">
			<label for="input_restaurant_name">Restaurant name:</label>
			<input type="text"  placeholder="Insert here..." name="name" id="input_restaurant_name" class="input_element">
		</div>
		<div class="input-label-pair">
			<label>Countries:</label>
			<div class="checkboxes_countries" id="div_countries"></div>
			<div class="input-label-pair" >
				<div class = "input_with_button">
					<select name="type" class="input_element" id="select_country">
						<option selected="selected" value="">Select a Country</option>
					</select>
					<div class="circle_button_wrapper">
						<svg class="quantity-button" id="add_country_button" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512"><!--!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d="M256 80c0-17.7-14.3-32-32-32s-32 14.3-32 32V224H48c-17.7 0-32 14.3-32 32s14.3 32 32 32H192V432c0 17.7 14.3 32 32 32s32-14.3 32-32V288H400c17.7 0 32-14.3 32-32s-14.3-32-32-32H256V80z"/></svg>
					</div>
				</div>
			</div>
		</div>
		<div class="input-label-pair">
			<label>Cuisine Types:</label>
			<div class="checkboxes_cuisine_types" id="div_cuisine_types">
			</div>
			<div class="input-label-pair">
				<div class = "input_with_button">
					<input type="text"  placeholder="Add new type"  name="name" id="input_cuisine_type" class="input_element">
					<div class="circle_button_wrapper">
						<svg class="quantity-button" id="add_type_button" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512"><!--!Font Awesome Free 6.5.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2024 Fonticons, Inc.--><path d="M256 80c0-17.7-14.3-32-32-32s-32 14.3-32 32V224H48c-17.7 0-32 14.3-32 32s14.3 32 32 32H192V432c0 17.7 14.3 32 32 32s32-14.3 32-32V288H400c17.7 0 32-14.3 32-32s-14.3-32-32-32H256V80z"/></svg>
					</div>
				</div>
			</div>
		</div>

	</form>
	<form class="condensed-form">
		<div class="input-label-pair div_manager_email bigger">
			<label for="select_manager_email">Manager email:</label>
			<select name="type" class="input_element" id="select_manager_email">
				<%--							<option value="manager1@studenti.unipd.it">manager1@studenti.unipd.it</option>--%>
			</select>
		</div>
		<div class="hours-wrapper">
			<div class="input-label-pair margin-input margin-right">
				<label for="input_opening_hour">Opening Hours:</label>
				<input type="time"  name="opening_at" class="input_element input_small" id="input_opening_hour" >
			</div>
			<div class="input-label-pair margin-input">
				<label for="input_closing_hour">Closing Hours:</label>
				<input type="time" name="closing_at" required id="input_closing_hour" class="input_element input_small">
			</div>
		</div>
		<div class="input-label-pair bigger">
			<label for="input_restaurant_description">Description:</label>
			<textarea name="name" id="input_restaurant_description" class="input_element" rows="10"></textarea>
		</div>
	</form>
</section>