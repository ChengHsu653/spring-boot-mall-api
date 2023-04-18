let productData = []

$(document).ready(function () {
  $.get(
    'https://1c14-118-163-218-100.ngrok-free.app/products',
    function (data) {
      console.log('Data received from ngrok backend:', data)
      // 在這裡處理從後端獲取的資料
    }
  )

  $.get('http://localhost:8080/products', function (data) {
    console.log('Data received from localhost backend:', data)
    // 在這裡處理從後端獲取的資料
    productData = data.results
    console.log(productData)
  })
})
