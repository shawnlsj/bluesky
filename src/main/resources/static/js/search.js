class SearchForm {
    static changeType(typeButton) {
        const form = typeButton.closest("[data-element-name='searchForm']");
        form.querySelector("[data-element-name='searchTypeField']").value =
            typeButton.getAttribute("data-search-type");

        form.querySelector("[data-element-name='currentSearchType']").textContent =
            typeButton.textContent;
    }
}

(function execute() {
    document.addEventListener("click", function (event) {
        if (event.target.getAttribute("data-element-name") === "searchTypeButton") {
            SearchForm.changeType(event.target);
        }
    });
})();