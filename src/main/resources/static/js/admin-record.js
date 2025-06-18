
const dayItems = document.querySelectorAll('.days li');
dayItems.forEach(item => {
    item.addEventListener('click', () => {
        dayItems.forEach(i => i.classList.remove('active'));
        item.classList.add('active');
    });
});