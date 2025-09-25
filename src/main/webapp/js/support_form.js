// @ts-nocheck
    document.addEventListener('DOMContentLoaded', function() {
    const categoryDropdown = document.getElementById('helpsOptions');
    const Help1 = document.getElementById('helpSupport-form');
    const Help2 = document.getElementById('bugSupport-form');
    const Help3 = document.getElementById('devSupport-form');

    function updateForm() {
        const selectedCategory = categoryDropdown.value;

        Help1.classList.add('hidden');
        Help2.classList.add('hidden');
        Help3.classList.add('hidden');

        if (selectedCategory === 'Help1') {
        Help1.classList.remove('hidden');

        } else if (selectedCategory === 'Help2') {
        Help2.classList.remove('hidden');

        } else if (selectedCategory === 'Help3') {
        Help3.classList.remove('hidden');
        }
    }
    updateForm();

    categoryDropdown.addEventListener('change', updateForm);
});