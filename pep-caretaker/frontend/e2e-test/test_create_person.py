import pytest
import time
import os
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait, Select
from selenium.webdriver.support import expected_conditions as EC

BASE_URL = "https://vm107.htl-leonding.ac.at"
ADD_URL = f"{BASE_URL}/residentAdd"

USERNAME = "admin"
PASSWORD = "admin123"


@pytest.fixture
def driver():
    options = webdriver.ChromeOptions()
    options.add_argument("--start-maximized")
    # options.add_argument("--headless=new")  # optional für CI

    driver = webdriver.Chrome(options=options)
    yield driver
    driver.quit()


def test_login_and_create_person(driver):
    wait = WebDriverWait(driver, 25)

    # ========================
    # 1️⃣ LOGIN (Keycloak)
    # ========================
    driver.get(BASE_URL)

    wait.until(EC.visibility_of_element_located((By.ID, "username"))).send_keys(USERNAME)
    driver.find_element(By.ID, "password").send_keys(PASSWORD)

    # ✔️ RICHTIGER LOGIN BUTTON
    wait.until(EC.element_to_be_clickable(
        (By.CSS_SELECTOR, "button[type='submit']")
    )).click()

    # Warten bis Redirect zurück zur App
    wait.until(EC.url_contains(BASE_URL))

    # ========================
    # 2️⃣ Seite öffnen
    # ========================
    driver.get(ADD_URL)

    wait.until(EC.visibility_of_element_located(
        (By.XPATH, "//h1[contains(.,'Neue Person anlegen')]")
    ))

    # ========================
    # 3️⃣ Person anlegen
    # ========================
    stamp = str(int(time.time()))
    first_name = f"Max{stamp}"
    last_name = f"Mustermann{stamp}"

    # Optional: Profilbild hochladen (nur wenn vorhanden)
    image_path = os.path.abspath("testdata/profile_picture.png")
    if os.path.exists(image_path):
        try:
            driver.find_element(By.CSS_SELECTOR, '[data-testid="profileImage"]').send_keys(image_path)
        except:
            pass

    driver.find_element(By.CSS_SELECTOR, '[data-testid="firstName"]').send_keys(first_name)
    driver.find_element(By.CSS_SELECTOR, '[data-testid="lastName"]').send_keys(last_name)

    Select(driver.find_element(By.CSS_SELECTOR, '[data-testid="role"]')).select_by_value("bewohner")

    # Geburtstag setzen (YYYY-MM-DD)
    dob_input = driver.find_element(By.CSS_SELECTOR, '[data-testid="dob"]')
    driver.execute_script("""
        arguments[0].value = '2000-01-01';
        arguments[0].dispatchEvent(new Event('input', {bubbles: true}));
        arguments[0].dispatchEvent(new Event('change', {bubbles: true}));
    """, dob_input)

    driver.find_element(By.CSS_SELECTOR, '[data-testid="password"]').send_keys("Test1234!")
    driver.find_element(By.CSS_SELECTOR, '[data-testid="roomNo"]').send_keys("101")

    # ========================
    # 4️⃣ Speichern
    # ========================
    submit_button = driver.find_element(By.CSS_SELECTOR, '[data-testid="submitPerson"]')
    wait.until(lambda d: submit_button.is_enabled())
    submit_button.click()

    # ========================
    # 5️⃣ Erfolg prüfen
    # ========================
    wait.until(EC.any_of(
        EC.url_contains("/residents"),
        EC.url_changes(ADD_URL)
    ))

    assert BASE_URL in driver.current_url
