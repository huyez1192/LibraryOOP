(async () => {
    const rawResponse = await fetch('/updatePassword', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: "ntnlinh41@vnu.edu.vn",
        password: "dabihack",
        confirm: "dabihack"
      })
    });
    const content = await rawResponse.json();
  
    console.log(content);
  })();