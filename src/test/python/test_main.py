#!/usr/bin/env python3

import unittest

from src.main.python.main import main

class TestStringMethods(unittest.TestCase):
    def test_main_output(self):
        self.assertEqual(main(), "Hello from main!")
    
if __name__ == '__main__':
    unittest.main()