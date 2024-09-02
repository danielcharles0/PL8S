function changeIcon(anchor) {
    var icon = anchor.querySelector("i");
    icon.classList.toggle('fa-arrow-up-a-z');
    icon.classList.toggle('fa-arrow-up-z-a');

    // anchor.querySelector("span").textContent = icon.classList.contains('fa-plus') ? "Read more" : "Read less";
}

/**
 * It reverses the list of elements, which initially arrives already ordered in AZ order.
 */
function sort_elements(){

    // Get container
    let container = document.getElementById("card_container");

    // Get elements in an array, so that it is not automatically updated by document changes
    let elements = Array.from(container.children);

    // Clear container content
    container.replaceChildren();

    // Insert elements in the container in the reversed order
    let elem;
    while((elem = elements.pop()) !== undefined) {

        container.insertAdjacentElement("beforeend", elem);

    }
}