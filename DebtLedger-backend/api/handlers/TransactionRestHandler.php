<?php

    class TransactionRestHandler
    {
        public function id($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT * FROM transaction WHERE id=".$id;

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("missing" => "transaction not found"); 
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function debtor($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT * FROM transaction WHERE debtor=".$id." ORDER BY date DESC";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("empty" => "no transactions found");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function creditor($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT * FROM transaction WHERE creditor=".$id." ORDER BY date DESC";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("empty" => "no transactions found"); 
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function getWithContact($user, $contact)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT * FROM transaction WHERE creditor=".$user." OR creditor=".$contact." ORDER BY date DESC";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("empty" => "no transactions found"); 
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function add($input)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $info = json_decode($input);
            
            if(isset($info->description))
            {
                $info->description = str_replace("'", "''", $info->description);
                $query = "INSERT INTO transaction (debtor, creditor, description, amount) VALUES (".$info->debtor.", ".$info->creditor.", '".$info->description."', ".$info->amount.")";
            }
            else
            {
                $query = "INSERT INTO transaction (debtor, creditor, amount) VALUES (".$info->debtor.", ".$info->creditor.", ".$info->amount.")";
            }
            
            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $query = "SELECT * FROM transaction WHERE id=".$result->insert_id;

                    if($result = $connection->query($query))
                    {
                        while($row = $result->fetch_assoc())
                        {
                            $response[] = $row;
                        }
                        $result->close();
                    }
                    else
                    {
                        $response[] = array("error" => $connection->error);
                    }
                }
                else
                {
                    $response[] = array("failure" => "transaction not added");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function addMultiple($input)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $transactions = json_decode($input);
            
            
            $query = "INSERT INTO transaction (debtor, creditor, description, amount) VALUES";

            foreach($transactions as $t) {
                // Escape any single quotes
                $t->description = str_replace("'", "''", $t->description);
                $query .= " (".$t->debtor.", ".$t->creditor.", '".$t->description."', ".$t->amount."),";
            }
            $query = substr($query, 0, -1);
                        
            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $response[] = array("success" => "transactions added");
                }
                else
                {
                    $response[] = array("failure" => "transactions not added");
                }
            }
            else 
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function computeBalances($id) {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT id, first_name, last_name, email, SUM(balances.balance) AS balance FROM
                        (SELECT contact.contact, debts.balance FROM contact LEFT JOIN (SELECT creditor AS contact, SUM(amount*-1) AS balance FROM transaction WHERE debtor = ".$id." GROUP BY creditor) AS debts ON contact.contact = debts.contact WHERE user = ".$id."
                        UNION
                        SELECT contact.contact, credits.balance FROM contact LEFT JOIN (SELECT debtor AS contact, SUM(amount) AS balance FROM transaction WHERE creditor = ".$id." GROUP BY debtor) AS credits ON contact.contact = credits.contact WHERE user = ".$id.") AS balances INNER JOIN user ON balances.contact = user.id
                        GROUP BY balances.contact ORDER BY first_name, last_name";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();

                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("empty" => "no contacts found"); 
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }
    }
?>