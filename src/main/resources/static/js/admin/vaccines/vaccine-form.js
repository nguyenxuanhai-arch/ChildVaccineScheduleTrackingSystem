
      function validateFileSize(input) {
        const maxSize = 10 * 1024 * 1024; // 10MB in bytes
        if (input.files[0].size > maxSize) {
          alert("File size must be less than 10MB");
          input.value = "";
        }
      }
 