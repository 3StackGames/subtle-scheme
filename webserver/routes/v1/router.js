var express = require('express');
var router = express.Router();

var user = require('./user')

/* API Welcome */
router.get('/', function(req, res, next) {
  res.json({
      success: true,
      message: 'Welcome to the Subtle-Scheme API v1'
  });
});

router.use('/users', user)

module.exports = router;